package com.secbro.drools.component;

import com.secbro.drools.utils.KieUtils;
import org.drools.core.io.impl.FileSystemResource;
import org.drools.core.spi.DataProvider;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.definition.KiePackage;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.Collection;

/**
 * Created by neo on 17/7/31.
 */

@Component
public class ReloadDroolsRules {
    @Autowired
    KieBase kieBase;
    public void reload() throws UnsupportedEncodingException {
        KieServices kieServices = getKieServices();
        KieFileSystem kfs = kieServices.newKieFileSystem();
        System.setProperty("drools.dateformat", "yyyy-MM-dd HH:mm");
        /*try {
            for (Resource file : getRuleFiles()) {
                kfs.write(ResourceFactory.newClassPathResource("rules/" + file.getFilename(), "UTF-8"));
            }
        } catch (IOException e) {
            System.out.println("读取规则失败");
            e.printStackTrace();
        }*/
        //kfs.write("src/main/resources/rules/temp.drl", loadRules());
        kfs.write(new FileSystemResource(new File("src/main/resources/rules/addpoint.drl")));
        kfs.write(new FileSystemResource(new File("src/main/resources/rules/subpoint.drl")));
        KieBuilder kieBuilder = kieServices.newKieBuilder(kfs).buildAll();
        Results results = kieBuilder.getResults();
        if (results.hasMessages(Message.Level.ERROR)) {
            System.out.println(results.getMessages());
            throw new IllegalStateException("### errors ###");
        }

        KieUtils.setKieContainer(kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId()));
        System.out.println("reload新规则重载成功");
    }

    private String loadRules() {
        // 从数据库加载的规则
        return "package plausibcheck.adress\n\n rule \"Postcode 6 numbers\"\n\n    when\n  then\n        System.out.println(\"规则2中打印日志：校验通过!\");\n end";

    }

    private KieServices getKieServices() {
        return KieServices.Factory.get();
    }

    public void reloadByHelper() throws UnsupportedEncodingException {

        KieHelper kieHelper = new KieHelper();

        kieHelper.addContent(loadRules(),ResourceType.DRL);

        Results results = kieHelper.verify();
        if (results.hasMessages(Message.Level.ERROR)) {
            System.out.println(results.getMessages());
            throw new IllegalStateException("### errors ###");
        }

        //KieBase kieBase = kieHelper.build();
        KieContainer kieContainer = kieHelper.getKieContainer();


        KieUtils.setKieContainer(kieContainer);
        System.out.println("新规则重载成功");
    }
    private Resource[] getRuleFiles() throws IOException {
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        //return FileSystemResource(new File("D::\\addpoint.drl"))
        return resourcePatternResolver.getResources("");
    }
}
