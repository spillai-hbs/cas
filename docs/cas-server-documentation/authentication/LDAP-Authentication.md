---
layout: default
title: CAS - LDAP Authentication
category: Authentication
---
{% include variables.html %}


# LDAP Authentication

LDAP integration is enabled by including the following dependency in the overlay:

{% include casmodule.html group="org.apereo.cas" module="cas-server-support-ldap" %}

## Configuration

{% include {{ version }}/ldap-authentication-configuration.md %}

## Password Policy Enforcement

To learn how to enforce a password policy for LDAP, please [review this guide](../installation/Password-Policy-Enforcement.html).

## Troubleshooting

To enable additional logging, modify the logging configuration file to add the following:

```xml
<Logger name="org.ldaptive" level="debug" additivity="false">
    <AppenderRef ref="console"/>
    <AppenderRef ref="file"/>
</Logger>
```