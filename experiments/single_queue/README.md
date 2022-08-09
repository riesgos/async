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

