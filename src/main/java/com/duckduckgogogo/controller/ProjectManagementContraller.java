package com.duckduckgogogo.controller;

import com.duckduckgogogo.domain.AzureFileHistory;
import com.duckduckgogogo.domain.Project;
import com.duckduckgogogo.domain.ProjectSupplierRelation;
import com.duckduckgogogo.domain.User;
import com.duckduckgogogo.services.AzureFileHistoryServer;
import com.duckduckgogogo.services.ProjectService;
import com.duckduckgogogo.services.ProjectSupplierRelationService;
import com.duckduckgogogo.services.UserService;
import com.duckduckgogogo.utils.PasswordEncodeAssistant;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.util.*;

@Controller
@RequestMapping("/console/project_management")
public class ProjectManagementContraller {
    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProjectSupplierRelationService projectSupplierRelationService;
    @Autowired
    private AzureFileHistoryServer azureFileHistoryServer;

    @RequestMapping("/search")
    @ResponseBody
    public Map<String, Object> search(@RequestParam(value = "search", defaultValue = "") String search,
                                      @RequestParam(value = "offset", defaultValue = "0") Integer offset,
                                      @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        Map<String, Object> p = new HashMap<>();

        Pageable pageable = new PageRequest(offset, limit, new Sort(Sort.Direction.DESC, "id"));
        Page<Project> page;
        if ("".equals(search.trim())) {
            page = projectService.findAll(pageable);
        } else {
            page = projectService.findAll(search.trim(), pageable);
        }
        if (page != null) {
            List<Project> projects = page.getContent();
            for (Project task : projects) {
                Set<User> supper = new HashSet<>();

                Set<User> suppliers = task.getSuppliers();
                for (User supplier : suppliers) {
                    ProjectSupplierRelation.Embeddabled embeddabled = new ProjectSupplierRelation.Embeddabled();
                    embeddabled.setProject_id(task.getId());
                    embeddabled.setUser_id(supplier.getId());
                    ProjectSupplierRelation psr = projectSupplierRelationService.findById(embeddabled);
                    if (psr != null) {
                        if (psr.getState() == null || psr.getState() != 0) {
                            supper.add(supplier);
                        }
                    }
                }
                task.setSuppliers(supper);
            }
        }

        p.put("total", page != null ? page.getTotalElements() : 0);
        p.put("rows", page != null ? page.getContent() : "");

        return p;
    }

    @RequestMapping("/supplier_list")
    @ResponseBody
    public List<User> supplierList() {
        return userService.findByRoleAndEnabled("S", true);
    }

    @RequestMapping("/get/{id}")
    @ResponseBody
    public Project get(@PathVariable Integer id) {
        return id != null ? projectService.findById(id) : null;
    }

    @PostMapping("/assign_to")
    @ResponseBody
    public Map<String, Object> assignTo(@Valid Project project, @RequestParam Integer supplier) throws Exception {
        Map<String, Object> r = new HashMap<>();
        Map<String, String> message = new HashMap<>();
        Project mark = projectService.findById(project.getId());

        if (mark != null && (mark.getState().equals(Project.NOT_ASSIGN) || mark.getState().equals(Project.ASSIGNED))) {
            User pm = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            mark.setVersion(project.getVersion());
            mark.setProjectManager(userService.findById(pm.getId()));
            Set<User> suppliers = mark.getSuppliers();
            User user = userService.findById(supplier);
            if (!suppliers.contains(user)) suppliers.add(user);
            mark.setSuppliers(suppliers);
            mark.setAssinged(new Date());
            mark.setState(Project.ASSIGNED);

            ProjectSupplierRelation.Embeddabled embeddabled = new ProjectSupplierRelation.Embeddabled();
            embeddabled.setProject_id(mark.getId());
            embeddabled.setUser_id(user.getId());
            ProjectSupplierRelation projectSupplierRelation = projectSupplierRelationService.findById(embeddabled);
            if (projectSupplierRelation != null) {
                if (projectSupplierRelation.getState() != null && projectSupplierRelation.getState() == 0) {
                    projectSupplierRelation.setState(null);
                }
                projectSupplierRelationService.save(projectSupplierRelation);
            }
        } else {
            message.put("WARNING", "Current state is not editable.");
        }
        if (message.isEmpty()) {
            projectService.save(mark);

            r.put("status", "SUCCEED");
        } else {
            r.put("status", "FAILED");
            r.put("message", message);
        }

        return r;
    }

    @PostMapping("/re_open")
    @ResponseBody
    public Map<String, Object> reOpen(@RequestParam Integer id) throws Exception {
        Map<String, Object> r = new HashMap<>();
        Map<String, String> message = new HashMap<>();
        Project mark = projectService.findById(id);

        if (mark != null && mark.getState().equals(Project.PENDING)) {
            mark.setState(Project.IN_PROGRESS);
        } else {
            message.put("WARNING", "Current state is not editable.");
        }
        if (message.isEmpty()) {
            projectService.save(mark);

            r.put("status", "SUCCEED");
        } else {
            r.put("status", "FAILED");
            r.put("message", message);
        }

        return r;
    }

    @PostMapping("/push_back")
    @ResponseBody
    public Map<String, Object> pushBack(@RequestParam Integer id) throws Exception {
        Map<String, Object> r = new HashMap<>();
        Map<String, String> message = new HashMap<>();
        Project mark = projectService.findById(id);

        if (mark != null && mark.getState().equals(Project.PENDING)) {
            mark.setState(Project.COMPLETED);
            mark.setPushback(new Date());
        } else {
            message.put("WARNING", "Current state is not editable.");
        }
        if (message.isEmpty()) {
            projectService.save(mark);

            r.put("status", "SUCCEED");
        } else {
            r.put("status", "FAILED");
            r.put("message", message);
        }

        return r;
    }

    @RequestMapping("/task_list")
    @ResponseBody
    public List<Project> taskList() {
        List<Project> tasks = new ArrayList<>();
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user != null) {
            user = userService.findById(user.getId());
            if (user != null && user.getRole().equals(User.ROLE_SUPPLIER)) {
                tasks = projectService.findBySupplier(user.getId());
                for (Project task : tasks) {
                    Set<User> supper = new HashSet<>();

                    Set<User> suppliers = task.getSuppliers();
                    for (User supplier : suppliers) {
                        ProjectSupplierRelation.Embeddabled embeddabled = new ProjectSupplierRelation.Embeddabled();
                        embeddabled.setProject_id(task.getId());
                        embeddabled.setUser_id(supplier.getId());
                        ProjectSupplierRelation psr = projectSupplierRelationService.findById(embeddabled);
                        if (psr != null) {
                            if (psr.getState() == null || psr.getState() != 0) {
                                supper.add(supplier);
                            }
                        }
                    }
                    task.setSuppliers(supper);
                }
            }
        }

        return tasks;
    }

    @PostMapping("/rejet")
    @ResponseBody
    public Map<String, Object> rejet(@RequestParam Integer id) throws Exception {
        Map<String, Object> r = new HashMap<>();
        Map<String, String> message = new HashMap<>();
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user != null) {
            Project mark = projectService.findById(id);

            if (mark != null && (mark.getState().equals(Project.ASSIGNED))) {
                ProjectSupplierRelation.Embeddabled embeddabled = new ProjectSupplierRelation.Embeddabled();
                embeddabled.setProject_id(mark.getId());
                embeddabled.setUser_id(user.getId());
                ProjectSupplierRelation projectSupplierRelation = projectSupplierRelationService.findById(embeddabled);
                projectSupplierRelation.setState(0);

                projectSupplierRelationService.save(projectSupplierRelation);
            } else {
                message.put("WARNING", "Current state is not editable.");
            }
            if (message.isEmpty()) {
                r.put("status", "SUCCEED");
            } else {
                r.put("status", "FAILED");
                r.put("message", message);
            }
        } else {
            throw new Exception();
        }

        return r;
    }

    @PostMapping("/commit")
    @ResponseBody
    public Map<String, Object> commit(@RequestParam Integer id) throws Exception {
        Map<String, Object> r = new HashMap<>();
        Map<String, String> message = new HashMap<>();
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user != null) {
            Project mark = projectService.findById(id);

            if (mark != null && mark.getState().equals(Project.ASSIGNED)) {
                mark.setState(Project.IN_PROGRESS);
                mark.setSuppliers(null);
                mark.setSupplier(userService.findById(user.getId()));
            } else {
                message.put("WARNING", "Current state is not editable.");
            }
            if (message.isEmpty()) {
                projectService.save(mark);

                r.put("status", "SUCCEED");
            } else {
                r.put("status", "FAILED");
                r.put("message", message);
            }
        } else {
            throw new Exception();
        }

        return r;
    }

    @PostMapping("/checkin")
    @ResponseBody
    public Map<String, Object> checkin(@RequestParam Integer id, MultipartHttpServletRequest request) throws Exception {
        Map<String, Object> r = new HashMap<>();
        Map<String, String> message = new HashMap<>();

        if (id != null) {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Project project = projectService.findById(id);
            if (project != null
                    && user != null
                    && user.getId() == project.getSupplier().getId()
                    && project.getState().equals(Project.IN_PROGRESS)) {
                MultipartFile mf = request.getFile("file");

                String filename = mf.getOriginalFilename();
                String suffix = filename.substring(filename.indexOf('.') + 1);
                // File.separator
                String folder = System.getProperty("java.io.tmpdir");
                String datetime = String.valueOf(new Date().getTime());
                String target = folder + PasswordEncodeAssistant.encode((datetime + filename).toCharArray()) + "." + suffix;
                File file = new File(target);
                try (FileInputStream fis = (FileInputStream) mf.getInputStream();
                     FileOutputStream fos = new FileOutputStream(target)) {
                    byte[] b = new byte[1024];
                    int i = fis.read(b);
                    while (i > -1) {
                        fos.write(b, 0, b.length);
                        fos.flush();
                        i = fis.read(b);
                    }
                } catch (Exception e) {
                    throw e;
                }

                //微软云Azure指向存储帐户的CloudStorageAccount对象（存储帐户可以包含多个容器，容器里包含文件夹和文件）
                CloudStorageAccount storageAccount;
                //创建 CloudBlobClient 对象的实例，该对象指向存储帐户中的 Blob 服务
                CloudBlobClient blobClient = null;
                //创建 CloudBlobContainer 对象的实例，该对象代表所访问的容器
                CloudBlobContainer container = null;
                String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=gbmsfiles;AccountKey=7PppWxCxuJjSpVOAlqg5AHt1FnlJim9QnvzPT5zTXukfHQcBX1ulRcZQsiUKLC0tQiEbw9P0TPuVNuV4mTwOTg==;EndpointSuffix=core.chinacloudapi.cn";
                //创建指向存储帐户的 CloudStorageAccount 对象的实例
                storageAccount = CloudStorageAccount.parse(storageConnectionString);
                //创建 CloudBlobClient 对象的实例，该对象指向存储帐户中的 Blob 服务
                blobClient = storageAccount.createCloudBlobClient();
                //创建 CloudBlobContainer 对象的实例，该对象代表所访问的容器(容器gbmscontainer已经在云端建好)
                container = blobClient.getContainerReference("gbmscontainer");
                //需获取对目标容器中 blob的引用，指向云端路径"1/1/"+sourceFile.getName()
                String filePath = "1/1/" + file.getName();
                CloudBlockBlob blob = container.getBlockBlobReference(filePath);
                //上传文件到云
                blob.uploadFromFile(file.getAbsolutePath());

                // 更新项目信息
                project.setTranslateFileUrl(filePath);
                project.setTranslateName(filename);
                project.setState(Project.PENDING);
                project.setCheckin(new Date());
                projectService.save(project);

                // 添加文件历史记录
                AzureFileHistory azureFileHistory = new AzureFileHistory();
                azureFileHistory.setProjectId(project.getId());
                azureFileHistory.setFileName(filename);
                azureFileHistory.setFilePath(filePath);
                azureFileHistory.setCreateByUser(user.getId());
                azureFileHistory.setCreate(new Date());
                azureFileHistoryServer.save(azureFileHistory);

            } else {
                message.put("WARNING", "Current state is not editable.");
            }
        } else {
            message.put("WARNING", "You did not select the task.");
        }

        if (message.isEmpty()) {
            r.put("status", "SUCCEED");
        } else {
            r.put("status", "FAILED");
            r.put("message", message);
        }

        return r;
    }

    @RequestMapping("/dowload/{type}/{id}")
    public void dowload(@PathVariable String type, @PathVariable Integer id, HttpServletResponse response) throws Exception {
        if (type != null && id != null) {
            Project project = projectService.findById(id);

            //存储帐户连接字符
            String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=gbmsfiles;AccountKey=7PppWxCxuJjSpVOAlqg5AHt1FnlJim9QnvzPT5zTXukfHQcBX1ulRcZQsiUKLC0tQiEbw9P0TPuVNuV4mTwOTg==;EndpointSuffix=core.chinacloudapi.cn";
            //创建指向存储帐户的 CloudStorageAccount 对象的实例
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);
            //创建 CloudBlobClient 对象的实例，该对象指向存储帐户中的 Blob 服务
            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
            //创建 CloudBlobContainer 对象的实例，该对象代表所访问的容器(容器gbmscontainer已经在云端建好)
            CloudBlobContainer container = blobClient.getContainerReference("gbmscontainer");

            CloudBlockBlob blob = null;
            String filePath = System.getProperty("java.io.tmpdir");
            String fileName = null;
            if (type.equals("translate")) {
                fileName = project.getTranslateName();
                filePath += project.getTranslateName();
                //需获取对目标容器中 blob的引用，指向云端路径"1/1/"+sourceFile.getName()
                blob = container.getBlockBlobReference(project.getTranslateFileUrl());
            } else if (type.equals("source")) {
                fileName = project.getSourceName();
                filePath += project.getSourceName();
                //需获取对目标容器中 blob的引用，指向云端路径"1/1/"+sourceFile.getName()
                blob = container.getBlockBlobReference(project.getSourceFileUrl());
            }
            if (fileName != null && !fileName.trim().isEmpty() && blob != null) {
                //下载文件到本地
                blob.downloadToFile(filePath);

                // 下载到客户端
                response.setHeader("content-type", "application/octet-stream");
                response.setContentType("application/octet-stream");
                response.setHeader("Content-Disposition", "attachment;filename=" + fileName.trim());
                byte[] buff = new byte[1024];
                try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(filePath)));
                     OutputStream os = response.getOutputStream()) {
                    int i = bis.read(buff);
                    while (i > -1) {
                        os.write(buff, 0, buff.length);
                        os.flush();
                        i = bis.read(buff);
                    }
                } catch (Exception e) {
                    throw e;
                }
            }
        }

    }

}
