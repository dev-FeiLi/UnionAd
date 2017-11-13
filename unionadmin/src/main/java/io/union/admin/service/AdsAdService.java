package io.union.admin.service;

import io.union.admin.common.redis.CacheAdsAd;
import io.union.admin.dao.AdsAdRepository;
import io.union.admin.entity.AdsAd;
import io.union.admin.entity.AdsPlan;
import io.union.admin.entity.AdsUser;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Shell Li on 2017/8/16.
 */
@Service
public class AdsAdService {

    final static Logger LOG = LoggerFactory.getLogger(AdsAdService.class);

    @Autowired
    private AdsAdRepository adsAdRepository;

    @Autowired
    private AdsUserService adsUserService;

    @Autowired
    private AdsPlanService adsPlanService;

    @Autowired
    private CacheAdsAd cacheAdsAd;

    final static int LOCK_STATUS = 9;
    final static int DELETE_STATUS = 8;

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public AdsAd findOne(long adid) {
        AdsAd ad = cacheAdsAd.single(adid);
        if (ad == null) {
            ad = adsAdRepository.findOne(adid);
        }
        return ad;
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public List<AdsAd> findByTypeAndWidthAndHeight(Integer type, Integer width, Integer height) {
        List<AdsAd> list = null;
        try {
            list = adsAdRepository.findAllByAdtypeAndWidthAndHeightAndAstatusNot(type, width, height, DELETE_STATUS);
        } catch (Exception e) {
            LOG.error("findAllByAdtypeAndWidthAndHeight error: ", e);
        }
        return list;
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public List<AdsAd> findAllByPlanIdOutOfStatus(long planid, int status) {
        Map<Long, AdsAd> map = cacheAdsAd.map();
        List<AdsAd> adlistUnlocked = new ArrayList<>();
        if (map != null && map.size() != 0) {
            for (Map.Entry<Long, AdsAd> entry : map.entrySet()) {
                AdsAd ad = entry.getValue();
                if (LOCK_STATUS == ad.getAstatus() || DELETE_STATUS == ad.getAstatus() || planid != ad.getPlanid())
                    continue;
                adlistUnlocked.add(ad);
            }
        }
        // redis里没有再读DB
        if (adlistUnlocked.size() == 0) {
            List<Integer> statusList = new ArrayList<>();
            statusList.add(status);
            statusList.add(DELETE_STATUS);
            adlistUnlocked = adsAdRepository.findAllByPlanidAndAstatusNotIn(planid, statusList);
        }

        return adlistUnlocked;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public long save(AdsAd adsAd) {
        try {
            if (adsAdRepository.save(adsAd) != null) {
                cacheAdsAd.save(adsAd);
            }
        } catch (Exception e) {
            LOG.error("AdsAd save error:", e);
        }
        return adsAd.getAdid();
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public long save(HttpServletRequest request, List<String> imageurl) {
        String planid = request.getParameter("planid"), adid = request.getParameter("adid"),
                adname = request.getParameter("adname"), adtype = request.getParameter("adtype"),
                size = request.getParameter("size"), urlfile = request.getParameter("urlfile"),
                adurl = request.getParameter("adurl"), limitdiv = request.getParameter("limitdiv"),
                limitdivheight = request.getParameter("limitdivheight"), limitpop = request.getParameter("limitpop"),
                deduction = request.getParameter("deduction"), adeffect = request.getParameter("adeffect"),
                dataType = request.getParameter("dataType");
        AdsAd adsAd;
        if (StringUtils.hasText(adid)) {
            adsAd = findOne(Long.valueOf(adid));
        } else {
            adsAd = new AdsAd();
            adsAd.setAddtime(new Date());
            adsAd.setAstatus(0);
        }
        AdsPlan adsPlan = adsPlanService.findOne(Long.valueOf(planid));
        adsAd.setPlanid(Long.valueOf(planid));
        adsAd.setAdname(adname);
        adsAd.setUid(adsPlan.getUid());
        adsAd.setAdtype(Integer.valueOf(adtype));
        adsAd.setAdurl(adurl);
        adsAd.setDataType(Integer.valueOf(dataType));

        String[] sizes = size.split("\\*");
        if (StringUtils.hasText(sizes[0])) {
            adsAd.setWidth(Integer.valueOf(sizes[0]));
        }
        if (StringUtils.hasText(sizes[1])) {
            adsAd.setHeight(Integer.valueOf(sizes[1]));
        }
        if (imageurl.size() > 0) {
            adsAd.setImageurl(imageurl.get(0));
        } else if (StringUtils.hasText(urlfile)) {
            adsAd.setImageurl(urlfile);
        }
        if (StringUtils.hasText(limitdiv)) {
            adsAd.setLimitdiv(Integer.valueOf(limitdiv));
        } else {
            adsAd.setLimitdiv(0);
        }
        if (StringUtils.hasText(limitdivheight)) {
            adsAd.setLimitdivheight(Integer.valueOf(limitdivheight));
        } else {
            adsAd.setLimitdivheight(0);
        }
        if (StringUtils.hasText(limitpop)) {
            adsAd.setLimitpop(Integer.valueOf(limitpop));
        } else {
            adsAd.setLimitpop(0);
        }
        if (StringUtils.hasText(deduction)) {
            adsAd.setDeduction(Integer.valueOf(deduction));
        } else {
            adsAd.setDeduction(0);
        }
        if (StringUtils.hasText(adeffect)) {
            adsAd.setAdeffect(Integer.valueOf(adeffect));
        } else {
            adsAd.setAdeffect(0);
        }
        if (adsAdRepository.save(adsAd) != null) {
            cacheAdsAd.save(adsAd);
        }
        return adsAd.getAdid();
    }

    /**
     * 初始化广告列表数据
     *
     * @param page
     * @param size
     * @return
     */
    public JSONObject getAllAdByPageSize(int page, int size) {
        JSONObject result = new JSONObject();
        Page<AdsAd> list = null;
        try {
            // 获得广告列表
            if (size == 0) size = 20;
            Sort sort = new Sort(Sort.Direction.DESC, "adid");
            Pageable pageable = new PageRequest(page, size, sort);
            list = adsAdRepository.findAllByAstatusNot(pageable, 8);

            // 准备前台显示数据
            result.put("total", list.getTotalElements());
            result.put("pageNumber", list.getSize());
            result.put("rows", prepareAdsDataRow(list.getContent()));
        } catch (Exception e) {
            LOG.error("getAllAdByPageSize with page error: ", e);
        } finally {
            return result;
        }
    }

    public JSONObject getAllAdByCondition(String searchCondition, String searchBy, String searchSize, int page, int size) {
        JSONObject result = new JSONObject();
        int width = 0;
        int height = 0;
        if (StringUtils.hasText(searchSize)) {
            String[] sizes = searchSize.split("\\*");
            width = Integer.valueOf(sizes[0]);
            height = Integer.valueOf(sizes[1]);
        }

        Page<AdsAd> list = null;
        try {
            if (size == 0) size = 20;
            Sort sort = new Sort(Sort.Direction.DESC, "adid");
            Pageable pageable = new PageRequest(page, size, sort);
            if (!StringUtils.hasText(searchBy) && !StringUtils.hasText(searchSize)) {
                list = adsAdRepository.findAllByAstatusNot(pageable, 8);
            } else {
                switch (searchCondition) {
                    case "adsid":
                        if (!StringUtils.hasText(searchSize)) {
                            list = "".equals(searchBy) ? adsAdRepository.findAllByAstatusNot(pageable, 8)
                                    : adsAdRepository.findAllByAdidAndAstatusNot(Long.valueOf(searchBy), 8, pageable);
                        } else {
                            list = "".equals(searchBy) ? adsAdRepository.findAllByWidthAndHeightAndAstatusNot(width, height, 8, pageable)
                                    : adsAdRepository.findAllByAdidAndWidthAndHeightAndAstatusNot(Long.valueOf(searchBy), width, height, 8, pageable);
                        }
                        break;
                    case "planid":
                        if (!StringUtils.hasText(searchSize)) {
                            list = "".equals(searchBy) ? adsAdRepository.findAllByAstatusNot(pageable, 8)
                                    : adsAdRepository.findAllByPlanidAndAstatusNot(Long.valueOf(searchBy), 8, pageable);
                        } else {
                            list = "".equals(searchBy) ? adsAdRepository.findAllByWidthAndHeightAndAstatusNot(width, height, 8, pageable)
                                    : adsAdRepository.findAllByPlanidAndWidthAndHeightAndAstatusNot(Long.valueOf(searchBy), width, height, 8, pageable);
                        }
                        break;
                    case "uid":
                        if (!StringUtils.hasText(searchSize)) {
                            list = "".equals(searchBy) ? adsAdRepository.findAllByAstatusNot(pageable, 8)
                                    : adsAdRepository.findAllByUidAndAstatusNot(Long.valueOf(searchBy), 8, pageable);
                        } else {
                            list = "".equals(searchBy) ? adsAdRepository.findAllByWidthAndHeightAndAstatusNot(width, height, 8, pageable)
                                    : adsAdRepository.findAllByUidAndWidthAndHeightAndAstatusNot(Long.valueOf(searchBy), width, height, 8, pageable);
                        }
                        break;
                    case "url":
                        if (!StringUtils.hasText(searchSize)) {
                            list = adsAdRepository.findAllByAdurlContainingAndAstatusNot(searchBy, 8, pageable);
                        } else {
                            list = "".equals(searchBy) ? adsAdRepository.findAllByWidthAndHeightAndAstatusNot(width, height, 8, pageable)
                                    : adsAdRepository.findAllByAdurlContainingAndWidthAndHeightAndAstatusNot(searchBy, width, height, 8, pageable);
                        }
                        break;
                    default:
                        break;
                }
            }

            // 准备前台显示数据
            result.put("total", list.getTotalElements());
            result.put("pageNumber", list.getSize());
            result.put("rows", prepareAdsDataRow(list.getContent()));
        } catch (Exception e) {
            LOG.error("getAllAdByCondition with page error :", e);
        }
        return result;
    }

    /**
     * 拼接出 前台datatable 需要的json对象。
     *
     * @param adList
     * @return
     */
    private JSONArray prepareAdsDataRow(List<AdsAd> adList) {
        JSONArray dataRow = new JSONArray();
        if (adList != null && adList.size() > 0) {
            for (AdsAd ad : adList) {
                JSONObject data = new JSONObject();
                AdsPlan plan = adsPlanService.findOne(ad.getPlanid());
                // TODO: 这里后期adsUserService里若是从redis里取的就不用改
                AdsUser user = adsUserService.findOne(ad.getUid());
                data = JSONObject.fromObject(ad);
                if (plan != null) {
                    data.put("planName", plan.getTitle());
                    data.put("payType", plan.getPayType());
                }
                if (user != null) {
                    data.put("userName", user.getUsername());
                }
                dataRow.add(data);
            }
        }

        return dataRow;
    }

    public Map<Long, AdsAd> findMap() {
        try {
            return cacheAdsAd.map();
        } catch (Exception e) {
            LOG.error("find map error: ", e);
        }
        return null;
    }
}

