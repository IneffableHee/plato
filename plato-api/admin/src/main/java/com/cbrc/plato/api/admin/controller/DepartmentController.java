package com.cbrc.plato.api.admin.controller;

import com.cbrc.plato.user.model.Department;
import com.cbrc.plato.user.model.TreeItem;
import com.cbrc.plato.user.service.IDepartmentService;
import com.cbrc.plato.util.response.PlatoBasicResult;
import com.cbrc.plato.util.response.PlatoResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/department")
public class DepartmentController {

@Autowired(required = false)
IDepartmentService idepartmentService;

    @RequestMapping("/create")
    @RequiresPermissions("department:create")
    public PlatoBasicResult create(Department department){
        if(null != idepartmentService.getByName (department.getDepartmentName ())){
            return PlatoResult.failResult("该部门已经存在！请另设部门！！！");
        }
        idepartmentService.intsertDepartment (department);
        return PlatoResult.successResult();
    }

    @RequestMapping("/delete")
    @RequiresPermissions("department:delete")
    public PlatoBasicResult delete(Integer id){
        if (null == idepartmentService.getBydepartmentId (id)){
            return  PlatoResult.failResult("该部门不存在！");
        }
        idepartmentService.deleteById (id);
        return PlatoResult.successResult();
    }

    @RequestMapping("/edit")
    @RequiresPermissions("department:update")
    public PlatoBasicResult edit(Department department){
        if (null == idepartmentService.getBydepartmentId (department.getDepartmentId ())){
            return  PlatoResult.failResult("该部门不存在！");
        }
        idepartmentService.update (department);
        return PlatoResult.successResult();
    }

    @RequestMapping("/list")
    @RequiresPermissions("department:list")
    public PlatoBasicResult list() {
        ArrayList<Department> IdepartmentList = idepartmentService.getDepartmentList ();

        //转换成结点
        List<TreeItem> allDept = IdepartmentList.stream ()
                .map (Department::deptNode)
                .collect (Collectors.toList ());
        //按照上级部门分组
        Map<Integer, List<TreeItem>> deptMap = allDept.stream ()
                .collect (Collectors.groupingBy (TreeItem::getParentId));
        //给每个部门绑定子部门
        allDept.forEach (node ->
                node.setChildren (deptMap.get (node.getId ()))
        );
        List<TreeItem> treeItem = deptMap.get (1);
        //BFS辅助队列
        List<TreeItem> queue = new ArrayList<> ();
//        queue.addAll (treeItem);
//        for (int i = 0; i < queue.size (); i++) {
//            TreeItem node = queue.get (i);
//            //遍历时先将子部门放入队列中
//            if (node.getChildren () != null) {
//                queue.addAll (node.getChildren ());
//            } else {
//                node.setChildren (new ArrayList<> ());
//            }
//        }
        treeItem.forEach (x -> System.out.println (x));
        return PlatoResult.successResult (treeItem);
    }
    @RequestMapping("/list1")
    @RequiresPermissions("department:list")
    public PlatoBasicResult list1(){
        ArrayList<Department> IdepartmentList = idepartmentService.getDepartmentList ();
        return PlatoResult.successResult(IdepartmentList);
    }

}


