---

## 📌 EasyJava 代码生成工具 README.md

# EasyJava 代码生成工具

一个基于 **Spring Boot** 的代码生成器，能够根据数据库表结构和配置文件，自动生成实体类、DAO、Service、Controller、MyBatis XML 映射文件，大幅提升开发效率。

## ✨ 功能特性
- 自动生成基础代码：实体类、DAO、Service、Controller、MyBatis XML
- 配置化设计：数据库连接、文件路径、后缀规则通过 `properties` 配置
- 模板化抽取：支持公共工具类、校验类、Base 父类生成
- 查询支持：生成的代码自带增删改查、分页查询、模糊搜索
- 错误处理：自动生成全局异常处理器与统一状态码

## 🛠️ 技术栈
- **后端**：Java、Spring Boot、MyBatis  
- **构建工具**：Maven  

## 🔑 架构与亮点
- **模板方法模式**：封装代码生成流程（读取元数据 → 渲染模板 → 输出文件），提升可扩展性。  
- **单例模式**：封装配置读取工具类，避免重复 IO。  
- **LinkedHashMap**：保持数据库字段顺序，支持生成标准化 CRUD 代码。  
- **统一异常与状态码**：生成项目即具备统一的错误处理能力。  

## 🚀 快速启动
1. 克隆仓库  
   ```bash
   git clone https://github.com/yourname/easyjava.git
   cd easyjava
2. 修改配置
	•	在 application.properties 中配置数据库连接与文件输出路径。
3. 运行项目
  ```bash
  mvn spring-boot:run
