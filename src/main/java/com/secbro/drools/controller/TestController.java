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
    @RequestMapping("/point")
    public PointDomain birthday() {
        KieSession kieSession = KieUtils.getKieContainer().newKieSession();
        //KieSession就是一个跟Drools引擎打交道的会话，其基于KieBase创建，它会包含运行时数据，包含“事实Fact”，并对运行时数据实时进行规则运算。通过KieContainer创建KieSession是一种较为方便的做法，其本质上是从KieBase中创建出来的。KieSession就是应用程序跟规则引擎进行交互的会话通道。
        //创建KieBase是一个成本非常高的事情，KieBase会建立知识（规则、流程）仓库，而创建KieSession则是一个成本非常低的事情，所以KieBase会建立缓存，而KieSession则不必。

        PointDomain pointDomain = new PointDomain();
        //PointDomain为事实Fact
        pointDomain.setBackMondy(200);
        pointDomain.setBackNums(2);
        pointDomain.setBillThisMonth(1);
        pointDomain.setBirthDay(true);
        pointDomain.setBuyMoney(1000);
        pointDomain.setUserName("jack");
        pointDomain.setPoint(100);

        kieSession.insert(pointDomain);
        int ruleFiredCount = kieSession.fireAllRules();
        //载入fact进行推理，Rete算法
        System.out.println("触发了" + ruleFiredCount + "条规则");
        //释放资源
        kieSession.dispose();
        return pointDomain;
    }
}
