// Define an application user.
let user = {
    user: 'appuser',
    pwd: 'appuser',
    roles: [{
        role: 'readWrite',
        db: 'appdb'
    }]
};
// Execute mongodb command to create the above user.
db.createUser(user);