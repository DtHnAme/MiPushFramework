package top.trumeet.mipushframework.help;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.xiaomi.xmsf.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import moe.shizuku.preference.Preference;
import moe.shizuku.preference.PreferenceGroup;
import moe.shizuku.support.helplib.HelpFragment;

/**
 * Created by Trumeet on 2018/2/8.
 */

public class SupportFragment extends HelpFragment {
    private static final String TAG = "SupportFragment";

    private LoadArticleTask mLoadTask;

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getPreferenceScreen()
                .findPreference(KEY_ISSUE)
                .setIntent(new Intent(Intent.ACTION_VIEW)
                        .setData(Uri.parse("https://github.com/NihilityT/MiPushFramework/issues")));
        getPreferenceScreen()
                .findPreference(KEY_MAIL)
                .setVisible(false);
        getPreferenceScreen().findPreference(KEY_TELEGRAM)
                .setIntent(new Intent(Intent.ACTION_VIEW)
                        .setData(Uri.parse("http://t.me/mipushframework")));


        Preference preference = new Preference(getContext());
        preference.setOrder(1);
        preference.setTitle(R.string.helplib_action_qq_group);
        preference.setIcon(R.drawable.helplib_feedback_telegram_24dp);
        preference.setIntent(new Intent(Intent.ACTION_VIEW)
                .setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D" + "2y-E1qhDtUckwLVKmidPX-j7gnO3x0ji")));
        ((PreferenceGroup) findPreference(KEY_CONTACT)).addPreference(preference);

        load();
    }

    private void load () {
        cancel();
        mLoadTask = new LoadArticleTask();
        mLoadTask.execute();
    }

    private void cancel () {
        if (mLoadTask != null) {
            if (!mLoadTask.isCancelled())
                mLoadTask.cancel(true);
            mLoadTask = null;
        }
    }

    private class LoadArticleTask extends AsyncTask<Void, Void, List<Article>> {

        @Override
        protected List<Article> doInBackground(Void... voids) {
            try {
                String[] articlesArray = getResources().getStringArray(R.array.help_articles);
                List<Article> articles = new ArrayList<>(articlesArray.length);
                for (String str : articlesArray) {
                    String[] info = str.split("\\|");
                    Log.d(TAG, "info: "+ Arrays.toString(info));
                    if (info.length != 2)
                        continue;

                    try {
                        int titleRes = R.string.class.getField(info[0])
                                .getInt(null);
                        int markdownRes = R.raw.class.getField(info[1])
                                .getInt(null);
                        articles.add(new Article(titleRes, markdownRes));
                    } catch (Exception e) {
                        Log.e(TAG, "Cannot read article", e);
                    }
                }
                return articles;
            } catch (Exception e) {
                Log.e(TAG, "Unable to load articles", e);
                return null;
            }
        }

        @Override
        public void onPostExecute (List<Article> articles) {
            if (articles == null)
                return;
            for (Article article : articles) {
                addArticle(article.getTitleRes(), article.getMarkdownRes());
            }
        }
    }
}
