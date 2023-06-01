# Sample Tracking Helper API

[![Build Status](https://travis-ci.com/qbicsoftware/sample-tracking-helper-lib.svg?branch=development)](https://travis-ci.com/qbicsoftware/sample-tracking-helper-lib)[![Code Coverage]( https://codecov.io/gh/qbicsoftware/sample-tracking-helper-lib/branch/development/graph/badge.svg)](https://codecov.io/gh/qbicsoftware/sample-tracking-helper-lib)

Sample Tracking Helper API, version 1.0.0-SNAPSHOT - An easy to use interface providing sample tracking update functions for ETL scripts

## Author
Created by Andreas Friedrich (andreas.friedrich@qbic.uni-tuebingen.de).

## Description

The sample tracking helper library should be only used by ETL scripts and provide a factory method for SampleTracking services. This services can be used to update a sample location status.

The usage is pretty simple:

```java
ServiceCredentials serviceCredentials = new ServiceCredentials()
serviceCredentials.user = "authuser"
serviceCredentials.password = "authpw"
URL serviceRegistryUrl = new URL("https://myservice-registry.de")

sampleTracker = SampleTracker.createQBiCSampleTracker(serviceRegistryUrl, serviceCredentials, location)
```

where the location object is a String representation in JSON:

```json
{
"location": {
        "name": "QBiC",
        "responsible_person": "QBiC Team",
        "responsible_person_email": "support@qbic.zendesk.com",
        "address": {
            "affiliation": "QBiC",
            "street": "Morgenstelle 10",
            "zip_code": 72076,
            "country": "Germany"
        },
        "sample_status": "DATA_AT_QBIC",
        "arrival_date": "2020-03-08T23:00Z"
    }
 }
```
