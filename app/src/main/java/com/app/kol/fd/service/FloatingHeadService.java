package com.app.kol.fd.service;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.IBinder;
import android.service.autofill.RegexValidator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.kol.fd.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.core.app.NotificationCompat;
import jp.co.recruit_lifestyle.android.floatingview.FloatingViewListener;
import jp.co.recruit_lifestyle.android.floatingview.FloatingViewManager;

public class FloatingHeadService extends Service implements FloatingViewListener {
    private static final String TAG = "FloatingHeadService";

    /**
     * Intent key (Cutout safe area)
     */
    public static final String EXTRA_CUTOUT_SAFE_AREA = "cutout_safe_area";

    /**
     * Notification ID
     */
    private static final int NOTIFICATION_ID = 9083150;

    /**
     * FloatingViewManager
     */
    private FloatingViewManager mFloatingViewManager;

    private String text = "";
    private String baseURL = "https://dictionaryapi.com/api/v3/references/learners/json";
    private String definationKey = "5939c7f7-1962-4205-b68e-0bcbff399ae6";

    protected String attachKey(String url, String key){
        return url + "?key="+key;
    }
    protected String attachWord(String url, String word){
        return url + "/"+word;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (mFloatingViewManager != null) {
            return START_STICKY;
        }
//        start listening for changes on the clipboard
        initOnCopy();
        final DisplayMetrics metrics = new DisplayMetrics();
        final WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        assert windowManager != null;
        windowManager.getDefaultDisplay().getMetrics(metrics);
        final LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.widget_chathead, null, false);;

        final ScrollView definitionBoard = layout.findViewById(R.id.panel);
        final ImageButton searchButton = layout.findViewById(R.id.search_close);
        final TextView mainWord = layout.findViewById(R.id.main_word);
        final TextView wordType = layout.findViewById(R.id.word_type);
        final TextView definition = layout.findViewById(R.id.definition);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: layout clicked");
                if (definitionBoard.getVisibility() == View.GONE){

                    if (text.equals("")) text = getClipBoardText();

                    if (text.matches("^[a-zA-Z]+$")){
                        mainWord.setText(text);
//                        wordType.setText(getWordType(text));
                        RequestQueue queue = Volley.newRequestQueue(FloatingHeadService.this);
                        String url = attachKey(attachWord(baseURL,text), definationKey);
// Request a string response from the provided URL.
                        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONArray responseArray = new JSONArray(response);
                                            JSONObject obj = (JSONObject) responseArray.get(0);
                                            String word_type = (String) obj.get("fl");
                                            JSONArray defs = (JSONArray) obj.get("shortdef");
                                            StringBuilder def = new StringBuilder();
                                            defs.length();
                                            for (int i = 0; i < defs.length(); i++) {
                                                def.append(defs.getString(i));
                                            }
                                            Log.i(TAG, "onResponse: response\n" + response);
                                            wordType.setText(word_type);
                                            definition.setText(def);
                                        } catch (JSONException e) {
                                            Log.e(TAG, "onResponse: Error parsign response",e );
                                        }

                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                definition.setText("Connection failed. Please check your network.");
                            }
                        });

// Add the request to the RequestQueue.
                        definition.setText("Loading...");
                        queue.add(stringRequest);
                    } else{
                        mainWord.setText(text);
                        wordType.setText("");
                        definition.setText("Sorry, we can not define such phrases for now.");
                    }
                    definitionBoard.setVisibility(View.VISIBLE);
                    searchButton.setImageResource(R.drawable.ic_clear);
                }else {
                    mainWord.setText("");
                    wordType.setText("");
                    definition.setText("");
                    definitionBoard.setVisibility(View.GONE);
                    searchButton.setImageResource(R.drawable.ic_text);
                }
                Log.i(TAG, getString(R.string.chathead_click_message));
            }
        });

        mFloatingViewManager = new FloatingViewManager(this, this);
        mFloatingViewManager.setFixedTrashIconImage(R.drawable.ic_trash_action);
        mFloatingViewManager.setActionTrashIconImage(R.drawable.ic_trash_action);
        mFloatingViewManager.setSafeInsetRect((Rect) intent.getParcelableExtra(EXTRA_CUTOUT_SAFE_AREA));
        final FloatingViewManager.Options options = new FloatingViewManager.Options();
        options.overMargin = (int) (16 * metrics.density);
        mFloatingViewManager.addViewToWindow(layout, options);

        startForeground(NOTIFICATION_ID, createNotification(this));
        return START_REDELIVER_INTENT;
    }

    private String getWordType(String text) {
        return "Verb";
    }

    private String getWordDefinition(String text) {

        return "This is a very long text. This is a very long text. This is a very long text. This is a very long text. " +
                "This is a very long text. This is a very long text. This is a very long text. This is a very long text. " +
                "This is a very long text. This is a very long text. This is a very long text. This is a very long text. ";
    }

    private String getClipBoardText() {
        String text = "";
        final ClipboardManager clipBoard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        if (clipBoard==null) return text;
        ClipData clipData = clipBoard.getPrimaryClip();
        if (clipData==null) return text;
        ClipData.Item item = clipData.getItemAt(0);
        text = item.getText().toString();
        return text;
    }
    /*
    * TODO
    * Move this to the main Activity
    * */
    public void initOnCopy(){
        final ClipboardManager clipBoard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        clipBoard.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                text = getClipBoardText();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        destroy();
        super.onDestroy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onFinishFloatingView() {
        stopSelf();
        Log.d(TAG, getString(R.string.finish_deleted));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onTouchFinished(boolean isFinishing, int x, int y) {
        if (isFinishing) {
            Log.d(TAG, getString(R.string.deleted_soon));
        } else {
            Log.d(TAG, getString(R.string.touch_finished_position, x, y));
        }
    }

    /**
     * Viewを破棄します。
     */
    private void destroy() {
        if (mFloatingViewManager != null) {
            mFloatingViewManager.removeAllViewToWindow();
            mFloatingViewManager = null;
        }
    }

    /**
     * 通知を表示します。
     * クリック時のアクションはありません。
     */
    private static Notification createNotification(Context context) {
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.default_floatingview_channel_id));
        builder.setWhen(System.currentTimeMillis());
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(context.getString(R.string.chathead_content_title));
        builder.setContentText(context.getString(R.string.content_text));
        builder.setOngoing(true);
        builder.setPriority(NotificationCompat.PRIORITY_MIN);
        builder.setCategory(NotificationCompat.CATEGORY_SERVICE);

        return builder.build();
    }
}
