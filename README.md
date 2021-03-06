# Cordaptor

Cordaptor is an open source project designed to address the needs of teams building decentralized applications
on [Corda](https://github.com/corda/corda) or integrating CorDapps with other systems. Corda is a great engine
for decentralized applications, but in order to communicate with a Corda node, teams have to develop bespoke
integrations using its Java client libraries. This comes with a steep learning curve, and adds complexity for
for teams working outside Java ecosystem, e.g. Node.js, .Net, or Python.

Cordaptor solves this problem by automatically creating REST API for any CorDapp running on a Corda node. There are
many tools that understand REST APIs in every technology stack, and teams can pick and choose what works for them.
Cordaptor also allows to decouple API users from the underlying Corda node in order to improve the availability,
reliability, and flexibility of the overall system.

## Features

* Zero-configuration deployment option as an embedded Corda service -- great for development and integration testing
* Generation of [OpenAPI 3](https://github.com/OAI/OpenAPI-Specification) JSON spec for the API endpoint based 
  on actual flows and contract state classes of available CorDapps
* Embedded [Swagger UI](https://swagger.io/tools/swagger-ui/) for exploring the API in your browser
* Docker-friendly standalone deployment option configurable via environment variables and compatible with
  Kubernetes secrets management
* Synchronous or asynchronous execution of Corda flows via the API
* Comprehensive API for node vault queries
* Flexible API security based on [PAC4J](https://www.pac4j.org/) allowing such authorization models as API keys,
  OpenID Connect, SAML 2, or just about anything else -- great for integrating managed services like AWS Cognito
* Full support for SSL out of the box
* Extensible architecture allowing bespoke features to be added as simply as dropping a JAR file into a directory

## Project status

At the moment Cordaptor is a technology preview made available to the wider community to gather feedback and identify
areas for improvement. Cordaptor's codebase is derived from a proprietary technology developed and battle-tested by
[Bond180](http://www.bond180.com) as part of its digital assets issuance and administration platform
[IAN](http://www.bond180.com). At the moment Cordaptor is not considered production-ready yet.

## Getting started

Cordaptor is designed from ground up to be unobstructive, so there is no code or configuration required!
Simply download the embedded bundle JAR file from releases and drop it into `cordapps` 
directory of your Corda node, restart it, and fire up your browser to access the Swagger UI.

Read more in [Getting started](./docs/getting-started.md) guide about other ways to get immediately
productive with Cordaptor.

## Next steps

* Learn how to [get started](./docs/getting-started.md) with Cordaptor
* Learn more about [using](./docs/how-to-use.md) Cordaptor
* Read about how Cordaptor fits into your [architecture](./docs/architecture.md)
* Understand how to [configure](./docs/configuration.md) Cordaptor
* Learn how to [create extensions](./docs/extensions.md) for Cordaptor

## Contributing

Cordaptor is an open-source project and contributions are welcome!

## License

[GNU Affero General Public License version 3 or later](./LICENSE)

SPDX:AGPL-3.0-or-later

Copyright (C) 2020 Bond180 Limited

**Important notice**: for the avoidance of doubt in the interpretation of the license terms,
the copyright holders commit to treat the following uses of Cordaptor as 'aggregate' as opposed to 'modified versions':
1. Deploying embedded Cordaptor bundle JAR file into a Corda node, regardless of whether it is a file
distributed as a binary or built from the source code, as long as the source code of all modules in the bundle
remains unmodified.
2. Annotating application code with annotation types provided by Cordaptor in order to fine-tune the behaviour
of Cordaptor components interacting with the application code.
3. Creating extensions for Cordaptor using it's published microkernel's and modules' APIs, where the
extensions' code is assembled into separate JAR files and made available for Cordaptor microkernel
to dynamically discover at runtime. For clarity's sake, code constituting published APIs must be appropriately
annotated, see [Extending Cordaptor](./docs/extensions.md) for further details.
4. Including Cordaptor as a component of a broader application architecture where other components interact with it
using network communication protocols regardless of how Cordaptor is deployed and configured.

The intent of using AGPL is to protect the interests of the Cordaptor user community and ensure any bug fixes
and important new features developed by some users become available to everyone else. It is not the intent of
using AGPL to force disclose of any proprietary application code relying on Cordaptor.