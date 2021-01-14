package xyz.ymtao.service.ztb;

import xyz.ymtao.entity.ZtbDocument;
import xyz.ymtao.util.R;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Author: ymt
 * @Description:
 * @CreateDate: 2021/1/11  14:39
 */
public interface ZtbService {
    public long updateZtb(ZtbDocument ztbDocument);

    public <T> void exportToExcel(List<T> list, String userId,HttpServletResponse response);

    public long removeData(ZtbDocument ztbDocument);
}
