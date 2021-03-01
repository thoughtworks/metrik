rs.initiate({'_id': 'rs0', 'members': [{'_id': 0, 'host': '127.0.0.1:27017'}]});
rs.status();

use 4-key-metrics;
db.createUser({ user: "4km", pwd: "4000km", roles: [{ role: "readWrite", db: "4-key-metrics" } ] } );
