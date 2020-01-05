package com.xxun.watch.storytall;

import android.view.View;

/**
 * Created by zhangjun5 on 2017/9/6.
 */

public class Const {
    public static final String MY_MUSIC_DIR = "Music";
    public static final String MY_BASE_DIR ="StoryTell";
    public static final String MY_LOG_DIR ="logs";
    public static final String MY_FILE_DIR ="files";
    public static final String SHARE_PREF_NAME = "storytell_share";
    public static final String SHARE_PREF_MEDIA_VOLUME = "share_pref_media_volume";
    public static final String SHARE_PREF_DEFAULT_STORY_STATUS = "share_pref_default_story_status";

    public static final String ACTION_BROAST_MEDIA_STATUS_PLAY = "brocast.action.media.status.play";
    public static final String ACTION_BROAST_MEDIA_STATUS_PAUSE = "brocast.action.media.status.pause";
    public static final String ACTION_BROAST_MEDIA_STATUS_DELETE = "brocast.action.media.status.delete";
    public static final String ACTION_BROAST_MEDIA_SONG_STATUS = "brocast.action.media.song.status";
    public static final String ACTION_BROAST_MEDIA_SONG_STATUS_REQ = "brocast.action.media.song.status.req";
    public static final String ACTION_BROAST_STORY_BEGIN = "com.xiaoxun.xxun.story.start";
    public static final String ACTION_BROAST_STORY_PREVIEW_FINISH = "com.xiaoxun.xxun.story.preview.finish";
    public static final String ACTION_BROAST_STORY_CHANGE_SOUND = "com.xiaoxun.xxun.story.change.sound";
		public static final String ACTION_BROAST_STORY_CHANGE_LIST_DATA = "com.xiaoxun.xxun.story.change.list.data";

    public static final String MEDIA_STORY_STATUE_INTENT_DATA = "song_name";
    public static final String MEDIA_STORY_STATUE_PLAY_INFO = "is_play";
    public static final String MEDIA_STORY_STATUE_SOUND_CHANGE = "sound_change";


    public static final String SUFFIX_TMP_FILE = ".tmp";

    public static final int STORY_DOWNLOAD_PROGRESS = 0x100;
    public static final int STORY_DOWNLOAD_FAIL = 0x100;
    public static final int STORY_DOWNLOAD_CANCLE = 0x100;
    public static final int STORY_DOWNLOAD_SUCCESS = 0x100;

    public static final String KEY_NAME_STORY_WIFI_ONLY ="story_dl_opt";
    public static final String KEY_NAME_STORY_PLAY_LIST = "story_play_list";

    public static final String KEY_NAME_EID = "EID";
    public static final String KEY_NAME_GID = "GID";
    public static final String KEY_NAME_KEYS = "Keys";
    public static final String KEY_NAME_SID = "SID";
    public static final String KEY_NAME_LASTTS = "lastTS";
    public static final String KEY_NAME_ARRAY = "array";

    public static final int CID_MAPGET_MGET = 60051;
    public static final int CID_GETSTORY_LIST = 70171;


    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

}
