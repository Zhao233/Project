package com.duckduckgogogo.services;

import com.duckduckgogogo.domain.Trace;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TraceService extends JpaRepository<Trace, Long> {
	@Query("select trace from Trace trace"
           
            )
    List<Trace> findAllList();
	
	@Query("select trace from Trace trace where trace.id = (select max(t.id) from Trace t)"
	           
            )
	Trace findLatest();
	
	@Query(value =    
			"select * from ( " +			 
			"select count(t.strangerid) times,t.strangerid,s.name,s.imageid,s.feature from face_trace_inhouse t,face_inhouse s where t.strangerid = s.id group by t.strangerid,s.name,s.imageid,s.feature " +
			") s1,( " +
			"select strangerid strangerid2,max(tracedate) latestdate from face_trace_inhouse group by strangerid " +
			") s2 where s1.strangerid = s2.strangerid2 order by s2.latestdate desc "		
			,
	       nativeQuery = true	           
            )
	List<Object[]> queryTimes();
	
	@Query(value =    
			"select max(t.tracedate) maxdate from face_trace_inhouse t,face_inhouse s where t.strangerid = s.id and t.updatestatus = 1 and s.imageid = ?1"		
			,
	       nativeQuery = true	           
            )
	Object queryMaxdate(String imageid);
	
	@Query(value =    
			"select count(*) countnumber from face_trace_inhouse t"		
			,
	       nativeQuery = true	           
            )
	Object queryCount();
	
	@Query(value =    
			"select count(*) tt,DATE_FORMAT(date_add(tracedate,interval 8 hour),'%H') thedate from face_trace_inhouse"+
			" where date_add(tracedate,interval 8 hour) >= DATE_FORMAT('2018-05-01 00:00:00','%Y-%m-%d %H:%i' )"+
			  " and date_add(tracedate,interval 8 hour) <= DATE_FORMAT('2018-06-01 23:59:59','%Y-%m-%d %H:%i' )"+
			  " and strangerid = ?1 group by thedate"		
			,
	       nativeQuery = true	           
            )
	List<Object[]> queryTimesChart(long id);
}
