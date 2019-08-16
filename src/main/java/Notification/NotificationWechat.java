package Notification;

import Utils.Config;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class NotificationWechat implements INotification
{
    private static final String HttpUrlFormat = "https://sc.ftqq.com/%s.send";
    /**
     * server酱规定每天最多推送500条微信消息，由此可以算出每一条的发送最小间隔为：24 * 60 * 60 / 500 = 172.8秒
     */
    private static final int notifyInterval = 173 * 1000;
    private final CloseableHttpClient httpClient = HttpClients.createDefault();
    private final HttpPost request = new HttpPost();
    private long lastNotifyTime = 0;


    @Override
    public boolean notify(String title, String content)
    {
        final long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - lastNotifyTime < notifyInterval) {
            return false;
        }

        try
        {
            final URIBuilder builder = new URIBuilder(String.format(HttpUrlFormat, System.getProperty(Config.SecretKey)));
            builder.addParameter("text", title);
            builder.addParameter("desp", content);

            request.setURI(builder.build());
            httpClient.execute(request);

            this.lastNotifyTime = currentTimeMillis;

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            request.releaseConnection();
        }

        return false;
    }
}
