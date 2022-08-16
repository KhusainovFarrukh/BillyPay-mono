package kh.farrukh.billypay.apis.stats;

import kh.farrukh.billypay.global.paging.PagingResponse;

public interface StatsService {

    PagingResponse<Stats> getStatsList(Long billId, int pageNumber, int pageSize);

    Stats getStatsById(long id);

    Stats addStats(StatsDTO statsDTO);

    Stats updateStats(long id, StatsDTO statsDTO);

    void deleteStatsById(long id);
}
