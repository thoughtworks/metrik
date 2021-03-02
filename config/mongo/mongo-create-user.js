use 4-key-metrics;
db.createUser({ user: "4km", pwd: "4000km", roles: [{ role: "readWrite", db: "4-key-metrics" } ] } );
