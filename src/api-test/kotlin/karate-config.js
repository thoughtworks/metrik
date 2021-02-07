function fn() {
    var env = karate.env;
    
    if(!env) {
        env = "dev";
    }

    var config = {};

    if(env == "dev") {
        config.baseUrl = 'http://localhost:9000'
    }

    karate.configure('connectTimeout', 5000);
    karate.configure('readTimeout', 5000);

    return config;
}