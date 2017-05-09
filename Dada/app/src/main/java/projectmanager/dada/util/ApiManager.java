package projectmanager.dada.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import projectmanager.dada.model.Gender;
import projectmanager.dada.model.Location;
import projectmanager.dada.model.LoginInfo;
import projectmanager.dada.model.LoginResult;
import projectmanager.dada.model.RegisterInfo;
import projectmanager.dada.model.RegisterResult;
import projectmanager.dada.model.Task;
import projectmanager.dada.model.User;

/**
 * 和服务器相关联的相关接口均在此类中完成。
 */
public class ApiManager {
    private static ApiManager instance = null;
    public static ApiManager getInstance(){
        if(instance == null){
            instance = new ApiManager();
        }
        return instance;
    }


    /*************************************************************************************
     *
     * 以下是和User相关的操作
     *
     ************************************************************************************/

    /**
     * 本函数书用于像应用层提供登录的接口。应用层调取此方法并得到返回的对象。
     * 注意：如果任一步骤出错，将返回null
     * @param phone    登录所必需的账号
     * @param password 登录所必须的密码
     * @return         如果任一步骤出错，返回null。否则返回服务器返回的对象。
     */
    public LoginResult handleLogin(String phone, String password){
        try{
            URL url = new URL("http://10.0.2.2:12345/login");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type","application/json; charset=UTF-8");
            connection.connect();
            //POST请求
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            //登录信息
            LoginInfo li = new LoginInfo();
            li.setPhoneNum(phone);
            li.setPassword(password);
            Gson gson = new Gson();
            String str = gson.toJson(li);
            //写入
            out.write(str.getBytes("UTF-8"));//这样可以处理中文乱码问题
            System.out.println(str);
            out.flush();
            out.close();
            //读取响应
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String lines;
            StringBuffer sb = new StringBuffer("");
            while ((lines = reader.readLine()) != null) {
                lines = new String(lines.getBytes(), "utf-8");
                sb.append(lines);
            }
            System.out.println(sb);
            reader.close();
            // 断开连接
            connection.disconnect();
            return gson.fromJson(sb.toString(),LoginResult.class);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 本函数用于向应用层提供注册的接口。应用层调取此方法并得到返回的对象。
     * 注意：如果任一步骤出错，将返回null
     * @param phone     注册所必需的账号
     * @param password  注册所必需的密码
     * @return          如果任一步骤出错，返回null。否则返回服务器返回的对象。
     */
    public RegisterResult handleRegister(String phone, String password){
        try {
            URL url = new URL("http://10.0.2.2:12345/register");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type","application/json; charset=UTF-8");
            connection.connect();
            //POST请求
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            //注册的新账户对象
            RegisterInfo ri = new RegisterInfo();
            ri.setPhoneNum(phone);
            ri.setPassword(password);
            ri.setGender(Gender.NONE.getCode());
            Gson gson = new Gson();
            String str = gson.toJson(ri);
            System.out.println(str);
            //写入
            out.write(str.getBytes("UTF-8"));//这样可以处理中文乱码问题
            out.flush();
            out.close();
            //读取响应
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String lines;
            StringBuffer sb = new StringBuffer("");
            while ((lines = reader.readLine()) != null) {
                lines = new String(lines.getBytes(), "utf-8");
                sb.append(lines);
            }
            System.out.println(sb);
            reader.close();
            // 断开连接
            connection.disconnect();
            return gson.fromJson(sb.toString(),RegisterResult.class);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 本函数用于提供修改密码的接口
     * @param userId      被修改密码的用户id
     * @param oldPassword 用户的原始密码
     * @param newPassword 用户的新密码
     * @return            返回一个字符串，succeed或者failed
     */
    public String handleChangePassword(int userId,String oldPassword,String newPassword){
        try{
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost("https://relay.nxtsysx.net/changePassword/");
            List<NameValuePair> postParameters = new ArrayList<>();
            postParameters.add(new BasicNameValuePair("userId", "" + userId));
            postParameters.add(new BasicNameValuePair("oldPassword", oldPassword));
            postParameters.add(new BasicNameValuePair("newPassword", newPassword));
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postParameters,HTTP.UTF_8);
            request.setEntity(formEntity);
            HttpResponse response = client.execute(request);
            if(response.getStatusLine().getStatusCode() == 200) {
                InputStream is = response.getEntity().getContent();
                String str = convertStreamToString(is);
                JSONObject resultJson = new JSONObject(str);
                String result = resultJson.getString("result");
                if (result.equals("succeed")) {
                    return "succeed";
                }else{
                    return resultJson.getString("error");
                }
            } else {
                System.out.println("[Error] Change Password Process. Status Code:" +
                        response.getStatusLine().getStatusCode());
                return "failed";
            }
        }catch (Exception e){
            e.printStackTrace();
            return "failed";
        }
    }

    /**
     * 本类适用于更新用户信息的接口。
     * @param userId   待更新的用户的id
     * @param username 待更新的用户的新用户名
     * @param sex      准备更新的性别
     * @param avator   头像
     * @param bio      用户的签名
     * @return         返回更新后的User对象。
     */
    public User handleUpdateProfile(int userId,String username,int sex,String avator,String bio){
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost("https://relay.nxtsysx.net/updateProfile/");
            request.setHeader("Content-Type","application/x-www-form-urlencoded;charset=utf-8");
            List<NameValuePair> postParameters = new ArrayList<>();
            postParameters.add(new BasicNameValuePair("userId", "" + userId));
            postParameters.add(new BasicNameValuePair("username", username));
            postParameters.add(new BasicNameValuePair("sex", "" + sex));
            postParameters.add(new BasicNameValuePair("avatar", "" + avator));
            postParameters.add(new BasicNameValuePair("bio", "" + bio));
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postParameters, HTTP.UTF_8);
            request.setEntity(formEntity);
            System.out.println("[Tip]" +  convertStreamToString(request.getEntity().getContent()));
            HttpResponse response = client.execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                InputStream is = response.getEntity().getContent();
                String str = convertStreamToString(is);
                JSONObject resultJson = new JSONObject(str);
                String result = resultJson.getString("result");
                if(result.equals("succeed")){
                    String userString = resultJson.getString("user");
                    JSONObject userJson = new JSONObject(userString);
                    return parseUser(userJson);
                }else{
                    String error = resultJson.getString("error");
                    System.out.println("[Error] Update Profile process. Update profile fail. Error Message:" + error);
                    return null;
                }
            } else {
                System.out.println("[Error] Update profile Process. Status Code:" +
                        response.getStatusLine().getStatusCode());
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 通过用户的id来获取用户对象本身。
     * @param userId 准备获取的用户的id
     * @return       成功则返回对象本身。否则返回Null
     */
    public User handleGetUserById(int userId){
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet("https://relay.nxtsysx.net/getUser?userId=" + userId);
            HttpResponse response = client.execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                InputStream is = response.getEntity().getContent();
                String str = convertStreamToString(is);
                JSONObject resultJson = new JSONObject(str);
                String result = resultJson.getString("result");
                if(result.equals("succeed")){
                    String userString = resultJson.getString("user");
                    JSONObject userJson = new JSONObject(userString);
                    return parseUser(userJson);
                }else{
                    String error = resultJson.getString("error");
                    System.out.println("[Error] Get User process. Get User profile fail. Error Message:" + error);
                    return null;
                }
            } else {
                System.out.println("[Error] Update profile Process. Status Code:" +
                        response.getStatusLine().getStatusCode());
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public String handleUploadAvatars(File file){
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("https://relay.nxtsysx.net/updateProfile/");
            MultipartEntity multipartEntity = new MultipartEntity();
            FileBody bin = new FileBody(file);
            multipartEntity.addPart("avatar", bin);
            multipartEntity.addPart("userId", new StringBody(DataManager.getInstance().getCurrentUser().getUserId() + ""));
            httpPost.setEntity(multipartEntity);
            HttpResponse response = httpClient.execute(httpPost);
            Log.i("xwk", response.getStatusLine().getStatusCode() + "");
            if(response.getStatusLine().getStatusCode() == 200){
                InputStream is = response.getEntity().getContent();
                String str = convertStreamToString(is);
                JSONObject resultJson = new JSONObject(str);
                String result = resultJson.getString("result");

                if(result.equals("succeed")){
                    JSONObject jsonObject = resultJson.getJSONObject("user");
                    User user = parseUser(jsonObject);
                    DataManager.getInstance().setCurrentUser(user);
                    System.out.println(user.getAvatar());
                    return "SUCCESS";
                }else{
                    String error = resultJson.getString("error");
                    System.out.println("[Error] Get User process. Upload avatar fail. Error Message:" + error);
                    return "FAILURE";
                }
            }else {
                System.out.println("[Error] Upload avatar Process. Status Code:" +
                        response.getStatusLine().getStatusCode());
                return "FAILURE";
            }
        } catch (IOException | JSONException e1) {
            e1.printStackTrace();
        }


        return null;
    }

    public Bitmap getAvatarBitmap(String image){
        HttpGet httpGet = new HttpGet("https://relay.nxtsysx.net/avatars/" + image);
        HttpClient httpClient = new DefaultHttpClient();
        Bitmap pic = null;
        try {
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            pic = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return pic;
    }


    /*************************************************************************************
     *
     * 以下是和Task相关的操作
     *
     ************************************************************************************/

    /**
     * 获取某个用户发布的任务集
     * @param userId         对象用户的id
     * @param selectedStatus 要获取那种状态的任务
     * @param limit          数量限制为多少
     * @return               返回对应数量的任务集
     */
    public ArrayList<Task> handleGetPublishTasks(int userId,int selectedStatus,int limit){
        try{
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet("https://relay.nxtsysx.net/getPublishedTasks?userId=" + userId +"&status=" + selectedStatus);
            HttpResponse response = client.execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                InputStream is = response.getEntity().getContent();
                String str = convertStreamToString(is);
                JSONObject jsonObj = new JSONObject(str);
                String result = jsonObj.getString("result");
                if(result.equals("succeed")){
                    String tasksString = jsonObj.getString("tasks");
                    System.out.println("[Tip] " + tasksString);
                    JSONArray taskJsonArray = new JSONArray(tasksString);
                    ArrayList<Task> rtResultList = new ArrayList<>();
                    for(int i = 0;i < taskJsonArray.length();i++){
                        JSONObject taskJson = (JSONObject)taskJsonArray.get(i);
                        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm").create();
                        Task tempTask = gson.fromJson(taskJson.toString(), Task.class);
                        rtResultList.add(tempTask);
                    }
                    return rtResultList;
                }else{
                    System.out.println("[Error] Get My Publish Task Process Fail. Error:" + jsonObj.getString("error"));
                    return null;
                }
            }else{
                System.out.println("[Error] Get My Publish Task Process. Status Code:" +
                        response.getStatusLine().getStatusCode());
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取自己接受的任务集
     * @param userId         要获取的那个用户自己接受的任务集的id
     * @param selectedStatus 要哪种状态的任务
     * @param limit          数量限制为多少
     * @return               返回任务集的array list
     */
    public ArrayList<Task> handleGetAcceptTasks(int userId,int selectedStatus,int limit){
        try{
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet("https://relay.nxtsysx.net/getAcceptedTasks?userId=" + userId
                    + "&status=" + selectedStatus + "&limit=" + limit);
            HttpResponse response = client.execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                InputStream is = response.getEntity().getContent();
                String str = convertStreamToString(is);
                JSONObject jsonObj = new JSONObject(str);
                String result = jsonObj.getString("result");
                if(result.equals("succeed")){
                    String tasksString = jsonObj.getString("tasks");
                    JSONArray taskJsonArray = new JSONArray(tasksString);
                    ArrayList<Task> rtResultList = new ArrayList<>();
                    for(int i = 0;i < taskJsonArray.length();i++){
                        JSONObject taskJson = (JSONObject)taskJsonArray.get(i);
                        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm").create();
                        Task tempTask = gson.fromJson(taskJson.toString(), Task.class);
                        rtResultList.add(tempTask);
                    }
                    return rtResultList;
                }else{
                    System.out.println("[Error] Get My Accept Task Process Fail. Error:" + jsonObj.getString("error"));
                    return null;
                }
            }else{
                System.out.println("[Error] Get My Accepted Task Process. Status Code:" +
                        response.getStatusLine().getStatusCode());
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取坐标location附近radius范围内的未被接受的任务。这个方法还没写完
     * @param location 用户选中的坐标
     * @param radius   坐标radius范围内的点
     * @return 从服务器返回的任务list
     */
    public ArrayList<Task> handleGetNearbyTasks(Location location,double radius){
        try{
            Log.i("xwk", location.getLatitude() + " " + location.getLongitude() + " " + radius);
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet("https://relay.nxtsysx.net/getTasksAround?lat=" +
                    location.getLatitude() + "&lon=" + location.getLongitude() + "&radius=" + radius);
            HttpResponse response = client.execute(request);
            Log.i("xwk", response.getStatusLine().getStatusCode() + "");
            if (response.getStatusLine().getStatusCode() == 200) {
                Log.i("xwk", "come in");
                InputStream is = response.getEntity().getContent();
                String str = convertStreamToString(is);
                JSONObject jsonObj = new JSONObject(str);
                String result = jsonObj.getString("result");
                if(result.equals("succeed")){
                    String tasksString = jsonObj.getString("tasks");
                    JSONArray taskJsonArray = new JSONArray(tasksString);
                    ArrayList<Task> rtResultList = new ArrayList<>();
                    for(int i = 0;i < taskJsonArray.length();i++){
                        JSONObject taskJson = (JSONObject)taskJsonArray.get(i);
                        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm").create();
                        Task tempTask = gson.fromJson(taskJson.toString(), Task.class);
                        rtResultList.add(tempTask);
                    }
                    return rtResultList;
                }else{
                    System.out.println("[Error] Get My Accept Task Process Fail. Error:" + jsonObj.getString("error"));
                    return null;
                }
            }else{
                Log.i("xwk", "not come in");
                System.out.println("[Error] Get nearby Task Process. Status Code:"
                        + response.getStatusLine().getStatusCode());
                return null;
            }
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }



    }

    /**
     * 发布新任务的接口
     * @param task   发布的任务
     * @return             返回新创建的任务对象本身
     */
    public Task handlePublishTask(Task task){
        try{
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost("https://relay.nxtsysx.net/publishTask/");
            List<NameValuePair> postParameters = new ArrayList<>();
            postParameters.add(new BasicNameValuePair("title", "" + task.getTitle()));
            postParameters.add(new BasicNameValuePair("description", task.getDescription()));
            postParameters.add(new BasicNameValuePair("userId", "" + task.getPublisher().getUserId()));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String dateStr;
            if (task.getDeadline() != null) {
                dateStr = sdf.format(task.getDeadline());
            }else {
                dateStr = "";
            }
            postParameters.add(new BasicNameValuePair("deadline", "" + dateStr));
            postParameters.add(new BasicNameValuePair("longitude", "" + task.getLocation().getLongitude()));
            postParameters.add(new BasicNameValuePair("latitude", "" + task.getLocation().getLatitude()));
            postParameters.add(new BasicNameValuePair("locationDscp", "" + task.getLocation().getDescription()));
            JSONArray tagsJsonArr = new JSONArray(task.getTags());
            postParameters.add(new BasicNameValuePair("tags", tagsJsonArr.toString()));
            postParameters.add(new BasicNameValuePair("credit", "" + task.getCredit()));
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postParameters,HTTP.UTF_8);
            request.setEntity(formEntity);
            HttpResponse response = client.execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                InputStream is = response.getEntity().getContent();
                String str = convertStreamToString(is);
                System.out.println(str);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm").create();
                JsonReader jsonReader = new JsonReader(new StringReader(str));
                jsonReader.setLenient(true);
                JsonObject resultJson = new JsonParser().parse(jsonReader).getAsJsonObject();
                String result = resultJson.get("result").getAsString();
                if(result.equals("succeed")){
                    Task t = gson.fromJson(resultJson.get("task"), Task.class);
                    DataManager.getInstance().getCurrentUser().setCredit(t.getPublisher().getCredit());
                    return t;
                }else{
                    String error = resultJson.get("error").getAsString();
                    System.out.println("[Error] Publish Task process.Status Code 200 But Error Message:" + error);
                    return null;
                }
            }else{
                System.out.println("[Error] Publish Task Process. Status Code:" + response.getStatusLine().getStatusCode());
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }



    /**
     * 根据id来获取对应的Task对象
     * @param taskId 你想要获得的任务对象的id
     * @return       任意一步出错将返回null
     */
    public Task handleGetTaskById(int taskId){
        try{
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet("https://relay.nxtsysx.net/viewTask?taskId=" + taskId);
            HttpResponse response = client.execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                InputStream is = response.getEntity().getContent();
                String str = convertStreamToString(is);
                JSONObject resultJson = new JSONObject(str);
                String result = resultJson.getString("result");
                if(result.equals("succeed")){
                    String taskString = resultJson.getString("task");
                    JSONObject taskJson = new JSONObject(taskString);
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm").create();
                    Task tempTask = gson.fromJson(taskJson.toString(), Task.class);
                    return tempTask;
                }else{
                    String error = resultJson.getString("error");
                    System.out.println("[Error] View Task process. Error Message:" + error);
                    return null;
                }
            }else{
                System.out.println("[Error] View Task Process. Status Code:" +
                        response.getStatusLine().getStatusCode());
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 任务背景介绍
     * @param task 任务
     * @return
     */
    public Task handleEditTask(Task task){
        try{
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost("https://relay.nxtsysx.net/editTask/");
            List<NameValuePair> postParameters = new ArrayList<>();
            postParameters.add(new BasicNameValuePair("title", "" + task.getTitle()));
            postParameters.add(new BasicNameValuePair("description", task.getDescription()));
            postParameters.add(new BasicNameValuePair("userId", "" + task.getPublisher().getUserId()));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            postParameters.add(new BasicNameValuePair("deadline", "" + sdf.format(task.getDeadline())));
            postParameters.add(new BasicNameValuePair("longitude", "" + task.getLocation().getLongitude()));
            postParameters.add(new BasicNameValuePair("latitude", "" + task.getLocation().getLatitude()));
            postParameters.add(new BasicNameValuePair("locationDscp", "" + task.getLocation().getDescription()));
            JSONArray tagsJsonArr = new JSONArray(task.getTags());
            postParameters.add(new BasicNameValuePair("tags", tagsJsonArr.toString()));
            postParameters.add(new BasicNameValuePair("credit", "" + task.getCredit()));
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postParameters,HTTP.UTF_8);
            request.setEntity(formEntity);
            HttpResponse response = client.execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                InputStream is = response.getEntity().getContent();
                String str = convertStreamToString(is);
                JSONObject resultJson = new JSONObject(str);
                String result = resultJson.getString("result");
                if(result.equals("succeed")){
                    String taskString = resultJson.getString("task");
                    JSONObject taskJson = new JSONObject(taskString);
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm").create();
                    Task tempTask = gson.fromJson(taskJson.toString(), Task.class);
                    return tempTask;
                }else{
                    String error = resultJson.getString("error");
                    System.out.println("[Error] Edit Task process. Error Message:" + error);
                    return null;
                }
            }else{
                System.out.println("[Error] Edit Task Process. Status Code:" +
                        response.getStatusLine().getStatusCode());
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 接受任务的接口
     * @param taskId 要接受的任务id
     * @param userId 将要接受任务的用户id
     * @return 如果报错或者没有正常返回200，则返回null
     *         正常返回但是fail，返回错误报告
     *         正常且成功，则会返回“success”
     */
    public String handleAcceptTask(int taskId,int userId){
        try{
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost("https://relay.nxtsysx.net/acceptTask/");
            List<NameValuePair> postParameters = new ArrayList<>();
            postParameters.add(new BasicNameValuePair("taskId", "" + taskId));
            postParameters.add(new BasicNameValuePair("userId", "" + userId));
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postParameters,HTTP.UTF_8);
            request.setEntity(formEntity);
            HttpResponse response = client.execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                InputStream is = response.getEntity().getContent();
                String str = convertStreamToString(is);
                JSONObject resultJson = new JSONObject(str);
                String result = resultJson.getString("result");
                if(result.equals("succeed")){
                    return "succeed";
                }else{
                    String error = resultJson.getString("error");
                    System.out.println("[Error] Accept Task process. Error Message:" + error);
                    return error;
                }
            }else{
                System.out.println("[Error] Accept Task Process. Status Code:" +
                        response.getStatusLine().getStatusCode());
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 完成某个任务的接口
     * @param taskId 已接受的任务id
     * @param userId 接受任务的用户id
     * @return 如果报错或者没有正常返回200，则返回null
     *         正常返回但是fail，返回错误报告
     *         正常且成功，则会返回“success”
     */
    public String handleDoneTask(int taskId,int userId){
        try{
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost("https://relay.nxtsysx.net/doneTask/");
            List<NameValuePair> postParameters = new ArrayList<>();
            postParameters.add(new BasicNameValuePair("taskId", "" + taskId));
            postParameters.add(new BasicNameValuePair("userId", "" + userId));
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postParameters,HTTP.UTF_8);
            request.setEntity(formEntity);
            HttpResponse response = client.execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                InputStream is = response.getEntity().getContent();
                String str = convertStreamToString(is);
                JSONObject resultJson = new JSONObject(str);
                String result = resultJson.getString("result");
                if(result.equals("succeed")){
                    return "succeed";
                }else{
                    String error = resultJson.getString("error");
                    System.out.println("[Error] Done Task process. Error Message:" + error);
                    return error;
                }
            }else{
                System.out.println("[Error] Done Task Process. Status Code:" +
                        response.getStatusLine().getStatusCode());
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 取消某个任务的接口。
     * @param taskId 任务的id
     * @param userId 发起者用户的id
     * @return 如果报错或者没有正常返回200，则返回null
     *         正常返回但是fail，返回错误报告
     *         正常且成功，则会返回“success”
     */
    public String handleCancelTask(int taskId,int userId){
        try{
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost("https://relay.nxtsysx.net/cancelTask/");
            List<NameValuePair> postParameters = new ArrayList<>();
            postParameters.add(new BasicNameValuePair("taskId", "" + taskId));
            postParameters.add(new BasicNameValuePair("userId", "" + userId));
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postParameters,HTTP.UTF_8);
            request.setEntity(formEntity);
            HttpResponse response = client.execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                InputStream is = response.getEntity().getContent();
                String str = convertStreamToString(is);
                JSONObject resultJson = new JSONObject(str);
                String result = resultJson.getString("result");
                if(result.equals("succeed")){
                    return "succeed";
                }else{
                    String error = resultJson.getString("error");
                    System.out.println("[Error] Cancel Task process. Error Message:" + error);
                    return error;
                }
            }else{
                System.out.println("[Error] Cancel Task Process. Status Code:" +
                        response.getStatusLine().getStatusCode());
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 任务发布者确认任务已经完成
     * @param taskId 要确认的任务id
     * @param userId 发起者的用户id
     * @return
     */
    public String handleConfirmTask(int taskId,int userId){
        try{
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost("https://relay.nxtsysx.net/confirmTask/");
            List<NameValuePair> postParameters = new ArrayList<>();
            postParameters.add(new BasicNameValuePair("taskId", "" + taskId));
            postParameters.add(new BasicNameValuePair("userId", "" + userId));
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postParameters,HTTP.UTF_8);
            request.setEntity(formEntity);
            HttpResponse response = client.execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                InputStream is = response.getEntity().getContent();
                String str = convertStreamToString(is);
                JSONObject resultJson = new JSONObject(str);
                String result = resultJson.getString("result");
                if(result.equals("succeed")){
                    return "succeed";
                }else{
                    String error = resultJson.getString("error");
                    System.out.println("[Error] Confirm Task process. Error Message:" + error);
                    return error;
                }
            }else{
                System.out.println("[Error] Confirm Task Process. Status Code:" +
                        response.getStatusLine().getStatusCode());
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 某个用户放弃了自己接受了的任务
     * @param taskId 要放弃的任务id
     * @param userId 接受者的用户id
     * @return
     */
    public String handleQuitTask(int taskId,int userId){
        try{
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost("https://relay.nxtsysx.net/quitTask/");
            List<NameValuePair> postParameters = new ArrayList<>();
            postParameters.add(new BasicNameValuePair("taskId", "" + taskId));
            postParameters.add(new BasicNameValuePair("userId", "" + userId));
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postParameters,HTTP.UTF_8);
            request.setEntity(formEntity);
            HttpResponse response = client.execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                InputStream is = response.getEntity().getContent();
                String str = convertStreamToString(is);
                JSONObject resultJson = new JSONObject(str);
                String result = resultJson.getString("result");
                if(result.equals("succeed")){
                    return "succeed";
                }else{
                    String error = resultJson.getString("error");
                    System.out.println("[Error] Quit Task process. Error Message:" + error);
                    return error;
                }
            }else{
                System.out.println("[Error] Quit Task Process. Status Code:" +
                        response.getStatusLine().getStatusCode());
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    /*************************************************************************************
     *
     * 以下是解析对象相关的操作方法
     *
     ************************************************************************************/

    /**
     * 解析User数据
     */
    public User parseUser(JSONObject userJson) throws JSONException{
        String userIdString = userJson.getString("userId");
        int userId = Integer.parseInt(userIdString);
        String userPhoneString = userJson.getString("phone");
        String passwordString = "DefaultPassword";
        String userUsernameString = userJson.getString("username");
        String creditString = userJson.getString("credit");
        int credit = Integer.parseInt(creditString);
        String sexString = userJson.getString("sex");
        int sex = Integer.parseInt(sexString);
        String avatorString = userJson.optString("avatar");
        String bioString = userJson.getString("bio");
        User returnUser = new User(userId,userPhoneString,userUsernameString,passwordString,
                credit,sex,avatorString,bioString);
        return returnUser;
    }

    /**
     * 本方法用于将字节流转化成字符串。
     * 主要使用场景是，获取服务器的response之后，读取response中的content内容。
     * @param   is 从response中拿到的字节流
     * @return     从字节流中拿出的字符串
     */
    public String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
