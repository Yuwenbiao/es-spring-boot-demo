package com.example.demo.es;

import com.example.demo.entity.News;
import com.example.demo.entity.NewsSearchResult;
import org.frameworkset.elasticsearch.ElasticSearchException;
import org.frameworkset.elasticsearch.boot.BBossESStarter;
import org.frameworkset.elasticsearch.client.ClientInterface;
import org.frameworkset.elasticsearch.entity.ESDatas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * es操作示例
 *
 * @author ywb
 * @date 2020/3/8 20:39
 */
@Service
public class DocumentCrud {
    private Logger logger = LoggerFactory.getLogger(DocumentCrud.class);

    @Autowired
    private BBossESStarter bbossEsStarter;

    /**
     * DSL config file path
     */
    private String mapPath = "esmapper/demo.xml";

    /**
     * 创建索引，如果索引已经存在则删除重建
     */
    public void dropAndCreateAndGetIndice() {
        //Create a client tool to load configuration files, single instance multithreaded security
        ClientInterface clientUtil = bbossEsStarter.getConfigRestClient(mapPath);

        try {
            //To determine whether the indice demo exists, it returns true if it exists and false if it does not
            boolean exist = clientUtil.existIndice("test");

            //Delete mapping if the indice demo already exists
            if (exist) {
                String r = clientUtil.dropIndice("test");
                logger.debug("clientUtil.dropIndice(\"demo\") response:" + r);
            }

            //Create index demo
            clientUtil.createIndiceMapping("test",//The indice name
                    "createDemoIndice");//Index mapping DSL script name, defined createDemoIndice in esmapper/demo.xml

            String testIndice = clientUtil.getIndice("test");//Gets the newly created indice structure
            logger.info("after createIndiceMapping clientUtil.getIndice(\"test\") response:" + testIndice);
        } catch (ElasticSearchException e) {
            logger.error("dropAndCreateAndGetIndice error: ", e);
        }
    }

    /**
     * 添加更新文档
     */
    public void addAndUpdateDocument() {
        //Build a create/modify/get/delete document client object, single instance multi-thread security
        ClientInterface clientUtil = bbossEsStarter.getRestClient();
        //Build an object as index document
        News news = new News();
        news.setId(1L);//Specify the document id, the unique identity, and mark with the @ESId annotation. If the demoId already exists, modify the document; otherwise, add the document
        news.setAuthor("author2");
        news.setCategory("category2");
        news.setContent("content2");
        news.setPublishDate(LocalDate.now());

        //Add the document and force refresh
        String response = clientUtil.addDocument("test",//indice name
                "news",//idnex type
                news, "refresh=true");
        logger.info("add new Document, response = {}", response);

        //Get the document object according to the document id, and return the Demo object
        News getNews = clientUtil.getDocument("test",//indice name
                "news",//idnex type
                "1",//document id
                News.class);
        logger.info("getNews: {}", getNews);

        //update document
        news.setContent("update content");
        //Execute update and force refresh
        response = clientUtil.addDocument("test",//index name
                "news",//idnex type
                news, "refresh=true");
        logger.info("update response: {}", response);

        //Get the modified document object according to the document id and return the json message string
        response = clientUtil.getDocument("test",//indice name
                "news",//idnex type
                "1");//document id
    }

    public void deleteDocuments() {
        //Build a create/modify/get/delete document client object, single instance multi-thread security
        ClientInterface clientUtil = bbossEsStarter.getRestClient();
        //Batch delete documents
        clientUtil.deleteDocuments("test",//indice name
                "news",//idnex type
                new String[]{"1", "2"});//Batch delete document ids
    }

    /**
     * Use slice parallel scoll query all documents of indice demo by 2 thread tasks. DEFAULT_FETCHSIZE is 5000
     */
    public void searchAllPararrel() {
        ClientInterface clientUtil = bbossEsStarter.getRestClient();
        ESDatas<News> esDatas = clientUtil.searchAllParallel("test", News.class, 2);
    }


    /**
     * Search the documents
     */
    public NewsSearchResult search() {
        //Create a load DSL file client instance to retrieve documents, single instance multithread security
        ClientInterface clientUtil = bbossEsStarter.getConfigRestClient(mapPath);
        //Set query conditions, pass variable parameter values via map,key for variable names in DSL
        //There are four variables in the DSL:
        //        applicationName1
        //        applicationName2
        //        startTime
        //        endTime
        Map<String, Object> params = new HashMap<>();
        //Set the values of applicationName1 and applicationName2 variables
        params.put("author", "author2");
        params.put("category", "category2");

        //Execute the query
        ESDatas<News> esDatas =  //ESDatas contains a collection of currently retrieved records, up to 1000 records, specified by the size attribute in the DSL
                clientUtil.searchList("test/_search",//demo as the indice, _search as the search action
                        "searchDatas",//DSL statement name defined in esmapper/demo.xml
                        params,//Query parameters
                        News.class);//Data object type Demo returned


        //Gets a list of result objects and returns max up to 1000 records (specified in DSL)
        List<News> demos = esDatas.getDatas();

//        String json = clientUtil.executeRequest("demo/_search",//demo as the index table, _search as the search action
//                "searchDatas",//DSL statement name defined in esmapper/demo.xml
//                params);//Query parameters

//        String json = com.frameworkset.util.SimpleStringUtil.object2json(demos);
        //Gets the total number of records
        long totalSize = esDatas.getTotalSize();
        NewsSearchResult newsSearchResult = new NewsSearchResult();
        newsSearchResult.setNewsList(demos);
        newsSearchResult.setTotalSize(totalSize);
        return newsSearchResult;
    }
}
