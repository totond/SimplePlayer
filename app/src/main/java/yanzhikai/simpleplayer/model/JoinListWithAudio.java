package yanzhikai.simpleplayer.model;

import org.greenrobot.greendao.annotation.Id;

/**
 * author : yany
 * e-mail : yanzhikai_yjk@qq.com
 * time   : 2018/01/24
 * desc   :
 */

public class JoinListWithAudio {
    @Id
    private Long id;
    private Long listId;
    private Long audioId;
}
