## serf-connector-java
#### Library to streamline the communication between [serf](https://github.com/rikardbq/serf) and any Java based application that wants to consume it.

---

### Usage

#### Connector
**[ init ]** Initial setup of address, username, password and which database to communicate with.
```java
Connector conn = new Connector(
        "http://localhost:8080",
        "test_db",
        "test_user",
        "test_pass"
);
```

**[ query ]** Querying for data will return a list containing elements of the return value type supplied to the query method.
- A query accepts the following arguments:
  - Value type class
  - String query
  - An optional variable length arguments list for the parts (values to match the ?, in the order of appearance)
```java
List<SomeDataClass> data = conn.query(SomeDataClass.class, "SELECT * FROM testing_table WHERE id = ? OR id = ?;", 3, 5);
```

**[ mutate ]** Mutating data will return a MutationResponse type that contains fields for the number of rows affected and a field with the last insert row id.
- A mutation accepts the following arguments: 
  - String query
  - An optional variable length arguments list for the parts (values to match the ?, in the order of appearance)
```java
MutationResponse mutationResponse = conn.mutate("INSERT INTO testing_table(im_data, im_data_also) VALUES(?, ?)", "hello", 123);
```

---

#### Migrator
**[ init ]** Initial setup of migrations location
```java
Migrator migrator = new Migrator("./migrations");
```
- Migrations are simply .sql files located in the folder supplied as the argument and will look something like:
  ```SQL
  ALTER TABLE testing_table ADD COLUMN test_column TEXT;
  ```
  - The migrations are processed in order sorted by name, so maintaining a sane naming system accordingly is good practice. I.E a list of migrations can look like:
    - 1__add_col_test.sql
    - 2__add_col_username.sql
    - ...
  - Migration state is kept in the following location: */<migrations_location>/\_\_$gen.serf.state.migrations\_\_.jsonc*

**[ running migrations ]** Supply connector instance
- In order to run the migrations located in the migrations location specified during initialization an instance of Connector needs to be supplied.
```java
migrator.run(conn);
```
