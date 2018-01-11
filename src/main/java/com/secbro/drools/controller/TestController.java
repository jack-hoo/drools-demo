package com.secbro.drools.controller;

import com.secbro.drools.component.ReloadDroolsRules;
import com.secbro.drools.model.fact.AddressCheckResult;
import com.secbro.drools.model.Address;
import com.secbro.drools.model.fact.PointDomain;
import com.secbro.drools.utils.KieUtils;
import org.drools.core.reteoo.RuleBuilderFactory;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

@RequestMapping("/test")
@Controller
public class TestController {

    @Resource
    private ReloadDroolsRules rules;

    @ResponseBody
    @RequestMapping("/address")
    public void test() {
        KieSession kieSession = KieUtils.getKieContainer().newKieSession();

        Address address = new Address();
        address.setPostcode("994251");

        AddressCheckResult result = new AddressCheckResult();
        kieSession.insert(address);
        kieSession.insert(result);
        int ruleFiredCount = kieSession.fireAllRules();
        System.out.println("触发了" + ruleFiredCount + "条规则");

        if (result.isPostCodeResult()) {
            System.out.println("规则校验通过");
        }

        kieSession.dispose();
    }

    @ResponseBody
    @RequestMapping("/reload")
    public String reload() throws IOException {
        rules.reload();
        return "ok";
    }

    @ResponseBody
    @RequestMapping("/birthday")
    public PointDomain birthday() {
        System.out.println(System.getProperty("user.dir"));
        File file = new File("src/main/resources/rules/addpoint.drl");
        //file.mkdir();
        System.out.println(file.exists());
        KieSession kieSession = KieUtils.getKieContainer().newKieSession();
        PointDomain pointDomain = new PointDomain();
        pointDomain.setBackMondy(200);
        pointDomain.setBackNums(2);
        pointDomain.setBillThisMonth(1);
        pointDomain.setBirthDay(true);
        pointDomain.setBuyMoney(1000);
        pointDomain.setUserName("jack");
        pointDomain.setPoint(100);
        kieSession.insert(pointDomain);
        int ruleFiredCount = kieSession.fireAllRules();
        System.out.println("触发了" + ruleFiredCount + "条规则");
        kieSession.dispose();
        return pointDomain;
    }
}
