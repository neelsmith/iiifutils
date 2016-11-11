#!/usr/bin/env amm
/*
Ammonite script to convert a IIIF manifest to CITE Image model, and write
a four-column tab-delimited representaiton of the sequence of images.

Input:  the script takes a CITE URN (as a String value) for a CITE Image collection,
and a IIIF manifest (either in a local file or from a URL).

Output: The four output columns are:

  Caption Urn Rights Source


Requires:

- scala
- ammonite: http://www.lihaoyi.com/Ammonite/#ScalaScripts


Usage:  Supply either a --file or --url argument.  Collection URN is required so the parameter does not need to be named. Note Ammonite's unusual-looking syntax with leading -- at  the beginning of the argument list.

    iiifToCite.sc -- COLLECTION_URN [--file FILENAME] [--url MANIFESTURL]


Example:

    iiifToCite.sc \
        -- urn:cite:ecodices:bern87 \
        --url  http://www.e-codices.unifr.ch/metadata/iiif/bbb-0087/manifest.json \
        > bern87.tsv

*/

import scala.io.Source
// Use play library to parse json:
import $ivy.`com.typesafe.play::play-json:2.5.9`
import play.api.libs.json._


// CITE Image model, including notation of source of image
case class Img(caption: String, urn: String, rights: String, src: String)

// Serialize as tab-separated values
def toTsv(imgs: Array[Img]) = {
  val header = "Caption\tUrn\tRights\tSource\n"
  header + imgs.map( i => i.caption + "\t" + i.urn + "\t" + i.rights + "\t" + i.src).mkString("\n")
}

// Extract ordered list of image urls from parsed manifest.
def imgUrls(iiif: JsValue) = {
  val ress = (iiif  \\ "resource").toVector
  ress.map( r => (r \ "@id").as[String] )
}

// Extract ordered list of captions (labels) for a given Canvas.
def captionsForCanvas(canvas: JsValue, captionBase : String) = {
  canvas  match {
    case ja : JsArray => {
      val pageStrs = (ja \\ "label").toVector.map(_.as[String])
      pageStrs.map( s => captionBase + ": " + s)
    }
    case _ => Vector("JsValue not a JsArray array.")
  }
}

// Expect to find ONE canvas in a parsed manifest
def singleCanvas(iiif: JsValue) = {
  val canvList = (iiif \\ "canvases").toList
  require(canvList.size == 1)
  canvList(0)
}


@main
def iiifToCite(urn: String, url: String = "", f: String = ""  ) = {

  if (url.isEmpty && f.isEmpty) {
    throw new Exception("Usage: iifToCite.sc collectionUrn [ FILE | URL ] ")
  }

  val jsonText = {
    if (url.isEmpty) {
      Source.fromFile(f).getLines.mkString
    } else {
      Source.fromURL(url).getLines.mkString
    }
  }
  val iiif  = Json.parse(jsonText)

  // License and MS-level labelling. These will be
  // applied to each image in the CITE model.
  val lic = (iiif  \ "license").as[String]
  val ms = (iiif  \ "label").as[String]

  // Ordered list of URLs where we can retrieve full-sized images
  val urls = imgUrls(iiif)

  // Generate arbitrary URNs for each image
  val numList = 1 to urls.size toArray
  val urns = numList.map( i => urn + "." + i + ".v1")

  // Ordered list of captions.
  val canvas = singleCanvas(iiif)
  val caps = captionsForCanvas(canvas, ms)
  require( caps.size == urls.size)

  // Group urns, captions and source urls in a 3-Tuple
  val tiered = urns zip caps zip urls
  val triples = tiered.map{ case ((a,b), c) => (a,b,c) }
  // Map tuple to collection of Img objects
  val imgs = triples.map { case (urn,capt,srcUrl) => Img(capt,urn,lic,srcUrl) }
  println(toTsv(imgs))
}
