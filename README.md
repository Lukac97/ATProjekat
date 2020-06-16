# AT-Projekat
## Implemented agents
* Ping-pong agents
* Contract Net Protocol (CNP) Agents
* (does not work in cluster)
## Implemented rest requests
* GET /agents/test 
* GET /agents/running
  * gets all running agents
* GET /agents/classes
  * gets all Agent types
* PUT /agents/running/{agentType}/{agentName}
  * creates new agent 
* GET /agents/pingpong
  * tests Ping-Pong agents
* GET /agents/cnptest/{agentName}
  * tests CNP Agents
