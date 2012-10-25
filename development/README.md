# README

This directory contains configuration files and information for setting up a
local development environment, compliant with the coding style and rules in use
at XIAM Solutions B.V.

## Recommended local development environment
Latest versions are used, unless otherwise noted.
+ Ubuntu Desktop LTS / MacOS
+ Oracle JDK, version 6
+ Netbeans IDE
+ Maven 3 and Git available from shell

## Target runtime environment
Starting early 2010 all our production environments are based on this stack.
Primary (and preferred) OS is Debian 6. However, for EC2 we (currently) prefer
Amazon Linux AMI (Centos).
+ 64 bit Linux (Debian 6 or Amazon Linux AMI)
+ Oracle JDK, version 6
+ Tomcat 7

## Checkstyle configuration
Checkstyle configuration files can be found in this directory and are referenced
from the parent Maven POM. If you are using Eclipse, you have to configure
Eclipse using these files.
Note that Checkstyle warnings should be minimized. Checkstyle errors are always
forbidden and will make the build fail.

## Netbeans configuration
TODO
