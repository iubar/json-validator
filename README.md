[![Build Status](https://app.travis-ci.com/iubar/json-validator.svg?branch=master)](https://app.travis-ci.com/github/iubar/json-validator)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/629e2af7ae1e4f839dbb560fd3e32aa2)](https://app.codacy.com/manual/Iubar/json-validator/dashboard)

# Json Cli Validator
A tool to check one or more json files against its json schema

## How to use it
    java -jar json-validator-jar-with-dependencies.jar <schema-file> (<json folder>|<json file>)

## References
- [Introducing JSON](http://www.json.org/json-it.html)
- [Json-Schema.org](https://json-schema.org) [Understanding JSON Schema](https://json-schema.org/understanding-json-schema/index.html)
- [Specifications](http://json-schema.org/specification-links.html) 
- [Docs](https://json-schema.org/understanding-json-schema/)
- [Implementations](http://json-schema.org/implementations.html)

### Json validator - Implementations
- [Everit](https://github.com/everit-org/json-schema)
- [Justify](https://github.com/leadpony/justify) (the default strategy for that project)
- [Networknt](https://github.com/networknt/json-schema-validator)
- [json-schema-validator](https://github.com/java-json-tools/json-schema-validator) (not used)
- [snowy-json](https://github.com/ssilverman/snowy-json#using-snow-in-your-own-projects) (not used)

### Json schema genertor
- https://github.com/victools/jsonschema-generator (not used)

### Tools
- [JSONschema.Net (online schema generator)](https://jsonschema.net)
- [JSONschemaValidator.Net (online validator)](https://www.jsonschemavalidator.net)
- [JsonFormatter.org](https://jsonformatter.org/json-editor)
- [JsonEditorOnline.org](https://jsoneditoronline.org)

### Json libraries

- [A look at JSR 374 - Java API for JSON Processing (JSON-P)](https://golb.hplar.ch/2019/08/json-p.html)
- [jackson vs gson](https://www.baeldung.com/jackson-vs-gson)

### JSON-B and JSON-P

JSON-B is just a layer on top of JSON-P: it does the binding part, while it delegates all the raw JSON processing to JSON-P. 
You can mix and match any JSON-B implementation with any JSON-P implementation.

### Pay attention

* Nuove possibilità offerte da Json schema versione 2019-09: https://stackoverflow.com/questions/62957704/can-additionalproperties-apply-to-nested-objects-in-json-schema
(la versione attualmente utilizzata è draft-07)

## How to generate a schema from a class

* see https://github.com/mbknor/mbknor-jackson-jsonSchema
 
