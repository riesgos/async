# Objective 

- This is a variant of the architecture implemented in the async-project
- The objective is to explore the pro's and con's of other designs
- The design here differs from the base-project in the following points:
  - only one message queue <= makes adding more processes easier
  - intermediate products red from messages, not from db <= arguably removes a dependency between the nodes and the db


# Lessons learned

- Processes have those `WpsOptionInput`s. Why not have a fake-process that outputs all the options in such a `WpsOptionInput`?
- two identical messages can be sent close to each other, before the target-process has completed its calculations. This starts the process twice. 
  - Using queue and loop instead of a lock.
- When we move the full state along in every post, that leads to many messages being sent around.
  - How about having instead every wrapper remember incomplete input-parameter-sets? They can then fill them up later when more data becomes available.
  - Works. But this way, nodes become stateful.
  - So there are basically two strategies:
    - **All state in messages**: 
      - every node listens to every message
      - even if the message does not yet contain enough parameters for the node to run its WPS, the node still responds by enriching the message with all optional input-values that it knows
      - those enriched messages are then posted on a topic
      - once a message (which might have been enriched several times) finally does contain a complete parameter-set, the node runs its WPS and also posts that output as a new message.
      - *Pro*:all state in message queue
      - *Con*: this leads to some duplicate messages (that's not much of a problem though if the WPS outputs are cached)
    - **State in messages and in node-cache**:
      - every node remembers all past inputs it has gotten for a given processing-id.
      - once the message-data *plus* the local memory of past messages constitutes a full parameter-set, the process runs and posts the output to a topic.
      - *Pro*: few messages, no duplicates
      - *Con*: state both in message-queue and in node-cache

