# S-Query-examples
This repo hosts some examples that query the internal state of stateful operators in S-Query. (Based on Hazelcast Jet)

[S-Query](https://github.com/Jimver/S-Query) is an adaptation of Hazelcast Jet which supports querying the internal state.

## Structure
The Two most important are the `dh` and `order-payment-jobs` and `benchmark-getter-job` modules.

`order-payment-jobs` contains the Jet jobs for the E-Commerce example from the S-Query paper.
It contains several modules:

- `order-payment-job`
  Contains the job with stateful operators and measuring mechanism.
- `order-payment-query` Job which queries the state using SQL
- `order-payment-query-benchmark` Job which queries the state with SQL continuously
- `order-payment-shared` Contains shared code with other modules
- `direct-query-job` Contains the job which queries the state using direct object access
- `generic-query` Is a helper job which can query any table with any type of query with the latest snapshot ID.

The `dh` folder contains the Jet jobs for the dh order demo application, which will replace the E-Commerce example in the S-Query paper.
Modules:
- `dh-job` Job which contains the stateful operations happening on the stream and measures the time it takes to go from source to sink.
- (Still need to add query jobs)

The `benchmark-getter-job` is a Jet job which gets the 2PC times of S-Query/Jet. Internally (in S-Query) the time it takes to complete a 2 phase commit is measured and put to a List.
This job gets that list and prints the latencies to the console (in ns). Both phase 1 and phase 2 are individually measured.

The `scripts` folder contains several scripts for transferring the jars to a remote machine/multiple remote machines.

