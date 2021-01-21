package fourkeymetrics.service

import fourkeymetrics.exception.ApplicationException
import fourkeymetrics.model.PipelineType
import fourkeymetrics.pipeline.Jenkins
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class ConfigurationService {

    @Autowired
    private lateinit var jenkins: Jenkins

    fun verifyPipeline(url:String,username:String,credential:String,type: String){
        if (type == PipelineType.JENKINS.name) {
            jenkins.verifyPipeline(url, username,credential)
        }else{
            throw  ApplicationException(HttpStatus.BAD_REQUEST,"Pipeline type not support")
        }
    }
}