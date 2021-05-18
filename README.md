### Pre-requisite 

- Install and configure [AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-welcome.html)
- Install Serverless Application Model i.e. [SAM](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/what-is-sam.html)
- Install [docker](https://docs.docker.com/get-docker/)

### Verify the installations

#### Verify AWS CLI installation and its configuration

```
❯ aws --version
aws-cli/2.2.2 Python/3.9.5 Darwin/20.3.0 source/x86_64 prompt/off


❯ tree ~/.aws/
/Users/viswanath/.aws/
├── config
└── credentials
0 directories, 2 files


❯ bat ~/.aws/config
───────┬──────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────
       │ File: /Users/viswanath/.aws/config
───────┼──────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────
   1   │ [default]
   2   │ region = eu-west-1
   3   │ output = json

```

#### Verify installation of SAM 

```
❯ sam --version
SAM CLI, version 1.23.0
```

#### Verify installation of Docker 

```
❯ docker --version
Docker version 20.10.6, build 370c289
```

### Start MongoDB and localstack services 

Start the services defined in the [docker-compose.yml](./docker-compose.yml) by issuing the following command from project's root directory 

```
❯ docker-compose up --always-recreate-deps --remove-orphans --build
```
[LocalStack](https://github.com/localstack/localstack) provides an easy-to-use mock framework for developing Cloud applications. This means, can test AWS cloud resources on local machine.

Once the services are started, localstack can be verified by loading its [health-check URL](http://localhost:4566/health).

```
❯ http GET http://localhost:4566/health
HTTP/1.1 200
access-control-allow-headers: authorization,content-type,content-length,content-md5,cache-control,x-amz-content-sha256,x-amz-date,x-amz-security-token,x-amz-user-agent,x-amz-target,x-amz-acl,x-amz-version-id,x-localstack-target,x-amz-tagging
access-control-allow-methods: HEAD,GET,PUT,POST,DELETE,OPTIONS,PATCH
access-control-allow-origin: *
access-control-expose-headers: x-amz-version-id
connection: close
content-length: 730
content-type: application/json
date: Mon, 17 May 2021 13:22:21 GMT
server: hypercorn-h11

{
    "features": {
        "initScripts": "initialized",
        "persistence": "disabled"
    },
    "services": {
        "acm": "running",
        "apigateway": "running",
        "cloudformation": "running",
        "cloudwatch": "running",
        "dynamodb": "running",
        "dynamodbstreams": "running",
        "ec2": "running",
        "es": "running",
        "events": "running",
        "firehose": "running",
        "iam": "running",
        "kinesis": "running",
        "kms": "running",
        "lambda": "running",
        "logs": "running",
        "redshift": "running",
        "resource-groups": "running",
        "resourcegroupstaggingapi": "running",
        "route53": "running",
        "s3": "running",
        "secretsmanager": "running",
        "ses": "running",
        "sns": "running",
        "sqs": "running",
        "ssm": "running",
        "stepfunctions": "running",
        "sts": "running",
        "support": "running",
        "swf": "running"
    }
}
```

Mongo express; a web based administrative interface of MongoDB can be accessed on [http://localhost:8081](http://localhost:8081)

### Run and debug the application locally using SAM and local stack

- Generate a sample payload for a SQS event
  ```
    ❯ sam local generate-event sqs receive-message > ./events/event.json
    ❯ bat ./events/event.json
    ───────┬────────────────────────────────────────────────────────────
    │ File: ./events/event.json
    ───────┼────────────────────────────────────────────────────────────
    1 + │ {
    2 + │   "Records": [
    3 + │     {
    4 + │       "messageId": "19dd0b57-b21e-4ac1-bd88-01bbb068cb78",
    5 + │       "receiptHandle": "MessageReceiptHandle",
    6 + │       "body": "{\"id\":\"one\",\"name\":\"batman\",\"powers\":[\"brilliant\"]}",
    7 + │       "attributes": {
    8 + │         "ApproximateReceiveCount": "1",
    9 + │         "SentTimestamp": "1523232000000",
    10 + │         "SenderId": "123456789012",
    11 + │         "ApproximateFirstReceiveTimestamp": "1523232000001"
    12 + │       },
    13 + │       "messageAttributes": {},
    14 + │       "md5OfBody": "7b270e59b47ff90a553787216d55d91d",
    15 + │       "eventSource": "aws:sqs",
    16 + │       "eventSourceARN": "arn:aws:sqs:us-east-1:123456789012:MyQueue",
    17 + │       "awsRegion": "us-east-1"
    18 + │     }
    19 + │   ]
    20 + │ }
  ```

#### Create a test queue against localstack 
- Create a queue against local stack 
```
  ❯ aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name test_queue

  {
      "QueueUrl": "http://localhost:4566/000000000000/test_queue"
  }
```
- Fetch the URL of the queue given its name
```
  ❯ aws --endpoint-url=http://localhost:4566 sqs get-queue-url --queue-name test_queue

  {
      "QueueUrl": "http://localhost:4566/000000000000/test_queue"
  }
```
- Send a message to the queue 
```
  ❯ aws --endpoint-url=http://localhost:4566 sqs send-message --queue-url http://localhost:4566/000000000000/test_queue --message-body 'Test Message!'
  
  {
    "MD5OfMessageBody": "df69267381a60e476252c989db9ac8ad",
    "MessageId": "4dccf727-7219-334e-a08b-86dd15f6370b"
  }
```
- Receive the message sent
```
  ❯ aws --endpoint-url=http://localhost:4566 sqs receive-message --queue-url http://localhost:4566/000000000000/test_queue
  
  {
    "Messages": [
        {
            "MessageId": "4dccf727-7219-334e-a08b-86dd15f6370b",
            "ReceiptHandle": "nbxymwjrrdzgtwhbzthlaxeefcnlvbffparzgiqcanptgjnmlhqiqvunvkkjdlviemelpbmpydbsqjfeobsiyyypvjvdsnlyjnakrcgugtrnrpfwzuranxzaoljibezkqjqmfztexoaqmkrgkczyjxcfhoerfpajgzgkqxyztquirdnbuqixasgxu",
            "MD5OfBody": "df69267381a60e476252c989db9ac8ad",
            "Body": "Test Message!"
        }
    ]
  }
  
```  
- Delete the message with its _receipt handle_
```
  ❯ aws --endpoint-url=http://localhost:4566 sqs delete-message --queue-url http://localhost:4566/000000000000/test_queue --receipt-handle 'nbxymwjrrdzgtwhbzthlaxeefcnlvbffparzgiqcanptgjnmlhqiqvunvkkjdlviemelpbmpydbsqjfeobsiyyypvjvdsnlyjnakrcgugtrnrpfwzuranxzaoljibezkqjqmfztexoaqmkrgkczyjxcfhoerfpajgzgkqxyztquirdnbuqixasgxu'
```  
- Fetch the identifier of localstack as shown below 
  ```
  ❯ docker ps --all
    CONTAINER ID   IMAGE                               COMMAND                  CREATED          STATUS          PORTS                                                                                            NAMES
    6d4cdad9fe32   localstack/localstack-full:latest   "docker-entrypoint.sh"   38 minutes ago   Up 38 minutes   0.0.0.0:4566->4566/tcp, :::4566->4566/tcp, 0.0.0.0:4571->4571/tcp, :::4571->4571/tcp, 8080/tcp   sqs-lambda_localstack_1
    9c8508ad7aae   mongo-express                       "tini -- /docker-ent…"   38 minutes ago   Up 38 minutes   0.0.0.0:8081->8081/tcp, :::8081->8081/tcp                                                        sqs-lambda_mongo-express_1
    796ea6408408   mongo                               "docker-entrypoint.s…"   38 minutes ago   Up 38 minutes   0.0.0.0:27017->27017/tcp, :::27017->27017/tcp                                                    sqs-lambda_mongo_1
  ```
- Gather the network used by localstack container by passing in the identifier of the localstack container  
  ```
  ❯ docker inspect 6d4cdad9fe32 -f "{{json .NetworkSettings.Networks }}"
  {"sqs-lambda_default":{"IPAMConfig":null,"Links":null,"Aliases":["localstack","6d4cdad9fe32"],"NetworkID":"a971c69c21ae26e06ce1bfb3b5e50afd195fc041aa72ee0a130bd56cc8b23f08","EndpointID":"f28f7f0b8011015ef0414103f72a3862b000a3297c2d18c6a1522d924a297b58","Gateway":"172.26.0.1","IPAddress":"172.26.0.4","IPPrefixLen":16,"IPv6Gateway":"","GlobalIPv6Address":"","GlobalIPv6PrefixLen":0,"MacAddress":"02:42:ac:1a:00:04","DriverOpts":null}}
  ```
#### Run the application   
- Invoke the lambda function using an event file
  ```
    ❯ sam local invoke MySQSMongoLambdaFunction --event ./events/event.json --log-file ./target/sam-local.log --docker-network a971c69c21ae26e06ce1bfb3b5e50afd195fc041aa72ee0a130bd56cc8b23f08
  ```
- View the log file `./target/sam-local.log`  

The content of logs must look similar to one shown below. 

```
START RequestId: e32bdc32-89b8-47f5-a82c-7b435552d8ae Version: $LATEST
09:18:34,752 |-INFO in ch.qos.logback.classic.LoggerContext[default] - Could NOT find resource [logback-test.xml]
09:18:34,755 |-INFO in ch.qos.logback.classic.LoggerContext[default] - Could NOT find resource [logback.groovy]
09:18:34,757 |-INFO in ch.qos.logback.classic.LoggerContext[default] - Found resource [logback.xml] at [file:/var/task/logback.xml]
09:18:35,021 |-INFO in ch.qos.logback.classic.joran.action.ConfigurationAction - debug attribute not set
09:18:35,034 |-INFO in ch.qos.logback.core.joran.action.AppenderAction - About to instantiate appender of type [ch.qos.logback.core.ConsoleAppender]
09:18:35,069 |-INFO in ch.qos.logback.core.joran.action.AppenderAction - Naming appender as [CONSOLE]
09:18:35,277 |-WARN in ch.qos.logback.core.ConsoleAppender[CONSOLE] - This appender no longer admits a layout as a sub-component, set an encoder instead.
09:18:35,277 |-WARN in ch.qos.logback.core.ConsoleAppender[CONSOLE] - To ensure compatibility, wrapping your layout in LayoutWrappingEncoder.
09:18:35,277 |-WARN in ch.qos.logback.core.ConsoleAppender[CONSOLE] - See also http://logback.qos.ch/codes.html#layoutInsteadOfEncoder for details
09:18:35,281 |-INFO in ch.qos.logback.classic.joran.action.LoggerAction - Setting level of logger [com.example] to DEBUG
09:18:35,281 |-INFO in ch.qos.logback.classic.joran.action.LoggerAction - Setting additivity of logger [com.example] to false
09:18:35,281 |-INFO in ch.qos.logback.core.joran.action.AppenderRefAction - Attaching appender named [CONSOLE] to Logger[com.example]
09:18:35,283 |-INFO in ch.qos.logback.classic.joran.action.RootLoggerAction - Setting level of ROOT logger to ERROR
09:18:35,283 |-INFO in ch.qos.logback.core.joran.action.AppenderRefAction - Attaching appender named [CONSOLE] to Logger[ROOT]
09:18:35,283 |-INFO in ch.qos.logback.classic.joran.action.ConfigurationAction - End of configuration.
09:18:35,286 |-INFO in ch.qos.logback.classic.joran.JoranConfigurator@2d6e8792 - Registering current configuration as safe fallback point


  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                        

09:18:42.957 [main] DEBUG c.e.s.c.SuperHeroMessageConverter - Converting an incoming message to an object of type SuperHero ...
09:18:42.963 [main] DEBUG c.e.s.c.SuperHeroMessageConverter - Header name: aws-context, Header value lambdainternal.api.LambdaContext@56a4479a
09:18:42.966 [main] DEBUG c.e.s.c.SuperHeroMessageConverter - Header name: id, Header value f8980984-cc2b-2330-f025-3ff80da7f63d
09:18:42.966 [main] DEBUG c.e.s.c.SuperHeroMessageConverter - Header name: timestamp, Header value 1621329522951
09:18:42.967 [main] DEBUG c.e.s.c.SuperHeroMessageConverter - Extract the value of 'body' property of first 'Record' from 
 {
  "Records": [
    {
      "messageId": "19dd0b57-b21e-4ac1-bd88-01bbb068cb78",
      "receiptHandle": "MessageReceiptHandle",
      "body": "{\"id\":\"one\",\"name\":\"batman\",\"powers\":[\"brilliant\"]}",
      "attributes": {
        "ApproximateReceiveCount": "1",
        "SentTimestamp": "1523232000000",
        "SenderId": "123456789012",
        "ApproximateFirstReceiveTimestamp": "1523232000001"
      },
      "messageAttributes": {},
      "md5OfBody": "F84E8B888849FE2AF9D6AD78F26E4811",
      "eventSource": "aws:sqs",
      "eventSourceARN": "arn:aws:sqs:us-east-1:123456789012:MyQueue",
      "awsRegion": "eu-west-1"
    }
  ]
}
09:18:43.154 [main] DEBUG c.e.s.c.SuperHeroMessageConverter - Converting JSON '{"id":"one","name":"batman","powers":["brilliant"]}' to an entity of type SuperHero
09:18:43.275 [main] DEBUG c.e.s.c.ApplicationConfiguration - Imperative function received a super-hero message : SuperHero(name=batman, powers=[brilliant])
09:18:43.290 [main] DEBUG c.e.s.service.SuperHeroService - Updating super hero SuperHero(name=batman, powers=[brilliant]) ...
09:18:43.953 [Thread-5] DEBUG c.e.s.c.ApplicationConfiguration - Successfully persisted superhero with identifier one
END RequestId: e32bdc32-89b8-47f5-a82c-7b435552d8ae
REPORT RequestId: e32bdc32-89b8-47f5-a82c-7b435552d8ae	Init Duration: 0.22 ms	Duration: 9558.50 ms	Billed Duration: 9600 ms	Memory Size: 1024 MB	Max Memory Used: 1024 MB	
"one"
```

#### Clean-up  
- Delete the queue given its URL
```
  ❯ aws --endpoint-url=http://localhost:4566 sqs delete-queue --queue-url http://localhost:4566/000000000000/test_queue
```
- Docker compose services can be shut down gracefully by issuing the following command from the project's root directory

```
❯ docker-compose down --remove-orphans --rmi local -v
```
