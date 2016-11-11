# iiifutils

Utilities for working with data in [IIIF](http://iiif.io/) format.


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
