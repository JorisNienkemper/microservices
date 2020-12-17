# Microservices

## A simple microservices demo based on https://www.youtube.com/watch?v=ZcudSdi5qo4
Demo implements the basic principles of microservice architecture including

- Consul for service discovery
- Circuit breaker for outages
- Gateway for connecting things together
- Load balancing for multiple instances


**Red service** collects random numbers for the red team

**Blue service** collects random numbers for the blue team

**Voter** generates random numbers for the red and blue service

**Gateway** Connects everything together.

**Consul is required:**

`brew install consul`

Run

`nohup consul agent -server=true -bootstrap=true -ui -client=0.0.0.0 -bind=192.168.1.106 -data-dir=/tmp/consul &
`

Start all the services

http://localhost:7000/blue/votes

http://localhost:7000/red/votes

**Consul ui**
http://localhost:8500/ui/dc1/services
