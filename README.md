# Wiredcraft Back-end Developer Test

## Introduction
It is a simple LBS service that offers nearby searches, following/followers lists, and profile updates, according to the [Requirement](docs/REQ.md).


## Quick Start and Demos
### Prerequisite
1. MongoDB setup using docker-compose
2. 


## Under the hood
### Tech Stack
- **MongoDB** Use mongodb as the DB for faster searches and [geospatial](https://www.mongodb.com/docs/manual/reference/operator/aggregation/geoNear/) functions.
- **Springboot Framework** as the de factro for Java world to build any restful api things.
- Use jWT and **Auth0** as the oauth2 provider for prototype building and MVP.
- **Github Actions** are used for CI solution.
- Cloudflare Tunnel for free https access and CDN services.
- Docker for deployment and testing.
- **Thymeleaf** adds support for Server-Side View Rendering.
- **Sentry** for logging APM.



## System design and User Stories
 

### User API
/users

### OAuth2
Use auth0.com as the backend oauth provider.

### Followers/Friend List
Add a mongodb collection as the follower.

### Near me
Use mongodb geospatial search to do geo search around a specific geo points (aka geocaches). 


## Tips and Caveats

