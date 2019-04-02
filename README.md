
# The Car Seller

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/54ed58afcf20477ca4cfcfa3492a4b1d)](https://app.codacy.com/app/vadimbakaev/car-seller?utm_source=github.com&utm_medium=referral&utm_content=vadimbakaev/car-seller&utm_campaign=Badge_Grade_Settings)

> The service allows users to place new car adverts and view, modify and delete existing car adverts.

## Installing / Getting started

First off all you need to install MongoDB.
Follow the manual with the instruction: `https://docs.mongodb.com/manual/administration/install-community/`

Once MongoDB installed, run it using the following command:
```shell
sudo mongod
```

By default, MongoDB starts on port 27017.

## Run

To run all test and generate coverage report run:
```sbtshell
clean coverage test it:test coverageReport coverageAggregate
```

To start application use:
```sbtshell
run
```

List of available endpoints:

*   POST    /public/v1/adverts
*   PUT     /public/v1/adverts
*   GET     /public/v1/adverts/:id
*   DELETE  /public/v1/adverts/:id
*   GET     /public/v1/adverts

add car advert
```shell
curl --request POST \
  --url http://localhost:9000/public/v1/adverts \
  --header 'content-type: application/json' \
  --data '{
	"id": "18f126d7-708b-4640-83f2-7916b9ad0531",
	"title": "Audi A3 Avant",
	"fuel": "D",
	"price": 7000,
	"new": true,
	"mileage": 70000
}'
```

get car advert
```shell
curl --request GET \
  --url http://localhost:9000/public/v1/adverts/18f126d7-708b-4640-83f2-7916b9ad0531
```

update car advert
```shell
curl --request PUT \
  --url http://localhost:9000/public/v1/adverts \
  --header 'content-type: application/json' \
  --data '{
	"id": "18f126d7-708b-4640-83f2-7916b9ad0531",
	"title": "Opel Manta",
	"fuel": "D",
	"price": 3500,
	"new": true
}'
```

get all car adverts
```shell
curl --request GET \
  --url 'http://localhost:9000/public/v1/adverts?sort=new&desc=true'\
```

delete car advert
```shell
curl --request DELETE \
  --url http://localhost:9000/public/v1/adverts/18f126d7-708b-4640-83f2-7916b9ad0531
```
