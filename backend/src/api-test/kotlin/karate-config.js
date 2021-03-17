function fn() {
    var env = karate.env;
    
    if(!env) {
        env = "local";
    }

    var config = {};

    if(env == "local") {
        config.baseUrl = 'http://localhost:9000'
    }

    karate.configure('connectTimeout', 5000);
    karate.configure('readTimeout', 5000);

    return config;
}