# united-oauth2

# [oauth2](http://www.ruanyifeng.com/blog/2014/05/oauth_2_0.html)

## 应用登录（密码模式）

### 步骤一：获取access_token

```sh
POST /oauth/token HTTP/1.1
Host: sso.onefly.com
Authorization: Basic b3BlbmlkOjY5M2JiMDBhMTkxMzRlM2M5ZmM5OTBiYzk3NDJmNjE0
Content-Type: application/x-www-form-urlencoded

grant_type=password&scope=openid&username=cust&password=cust123
```

含以下头部信息

- Authorization: 表示请求授权方式，必选项，格式"Basic base64(client_id:client_secret)"
    - client_id: openid
    - client_secret: 693bb00a19134e3c9fc990bc9742f614

包含以下参数

- grant_type：表示授权类型，此处的值固定为"password"，必选项。
- username：表示用户名，必选项。
- password：表示用户的密码，必选项。
- scope：表示权限范围，可选项。

返回值

```json
{
    "access_token": "c10c0189-a8e3-4687-9977-c1ce42cf80dc",
    "token_type": "bearer",
    "refresh_token": "b41d79f7-3258-49b7-9be1-eae36d3142ba",
    "scope": "openid"
}
```

### 步骤二

根据步骤一中获取的access_token请求其他接口

请求格式如下

```sh
GET api/user HTTP/1.1
Host: sso.onefly.com
Authorization: Bearer c10c0189-a8e3-4687-9977-c1ce42cf80dc
```