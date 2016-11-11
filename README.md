# iiifutils

Utilities for working with data in [IIIF](http://iiif.io/) format.  Currently, two simple Ammonite scripts:


- `iiifToCite.sc`:  create simple .tsv data sets from IIIF manifests
- `getImageData.sc`:  download binary image data to appropriately named local files


## `iiifToCite.sc`

`iiifToCite.sc` is an Ammonite script to convert a IIIF manifest to an object representation of the CITE Image model.  The script writes a four-column tab-delimited representation of the sequence of images to standard output.

It has been tested against a number of manifests from [e-codices](http://www.e-codices.unifr.ch/en), and seems to work reliably with their very consistently structured manifest data.

### Usage

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


## `getImageData.sc`

Ammonite script to download binary data for a CITE Image collection
documented in a simple .tsv file.

Input:  the script takes a file with four tab-delimited columns, in the order generated by iiifToCite.sc.  The four columns are

    Caption Urn Rights Source

Optionally, you may specify a file name extension:  the default is `jpg`.

Output: A series of binary image files.  The file names are the collection-object-version identifiers of the image's CITE URN, with periods replaced by underscore.  Example:  an image with URN `urn:cite:ecodices:bern87.8.v1` using the default `jpg` filename extension will be named `bern87_1_v1.jpg`

Requires:

- scala
- ammonite: http://www.lihaoyi.com/Ammonite/#ScalaScripts


Usage:

    getImageData.sc TSVFILE [EXTENSION]


Examples:

Download all images in this .tsv catalog:

    getImageData.sc bern87.tsv

Resume downloading from the 59th image (index 58):

    getImageData.sc -- bern87.tsv   --startFrom 58
