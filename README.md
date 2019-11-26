# SPL2

 An implementation of an online book store with a delivery option.
 Written in java, dealing with concurrency and following a Micro-Service framework, that is consisted of
two parts: A Message-Bus and Micro-Services. Each Micro-Service is a thread that can exchange
messages with other Micro-Services using a shared object - the Message-Bus. A message can be either an action to be done or information that other micro-services require to continue their jobs.
