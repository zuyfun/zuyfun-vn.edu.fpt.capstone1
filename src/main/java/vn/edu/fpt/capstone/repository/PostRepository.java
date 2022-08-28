package vn.edu.fpt.capstone.repository;

import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.edu.fpt.capstone.dto.DashBoardData;
import vn.edu.fpt.capstone.model.PostModel;
import vn.edu.fpt.capstone.response.HouseHistoryResponse;
import vn.edu.fpt.capstone.response.HouseResponse;

@Repository
public interface PostRepository extends JpaRepository<PostModel, Long> {
	@Query("SELECT pm FROM PostModel pm WHERE pm.enable = true AND pm.createdBy = ?1")
	List<PostModel> findAllByUsername(String username);

	@Query("SELECT pm FROM PostModel pm WHERE pm.enable = true ORDER BY pm.createdDate DESC")
	List<PostModel> findAllQuery();
	
	@Query("SELECT new vn.edu.fpt.capstone.response.HouseHistoryResponse(p.postType.type, p.cost, p.startDate, p.endDate, p.status) FROM PostModel p WHERE p.createdBy = ?1 AND p.house.id = ?2")
	List<HouseHistoryResponse> getListHouseHistory(String username, Long id);

	@Query("SELECT COUNT(u) FROM PostModel u")
	int getTotalAmountPost();

	@Query("SELECT COUNT(u) FROM PostModel u WHERE u.createdBy = ?1 AND u.status != 'DELETED' AND u.house.enable = true AND u.enable = true")
	int getTotalAmountPostHost(String createdBy);

	@Query("SELECT pm FROM PostModel pm WHERE pm.enable = true AND pm.house.user.id = ?1 AND pm.house.enable = true ORDER BY pm.createdDate DESC")
	List<PostModel> findAllPostByUserId(Long id);

	@Query(value = "SELECT p.* FROM favorite f JOIN post p ON f.postId = p.id WHERE f.userid= :userId", nativeQuery = true)
	List<PostModel> findAllFavoritePostByUserId(@Param("userId") Long userId);

	@Query(value="SELECT p.* FROM post p"
			+ " left join (SELECT postid as pid, AVG(f.rating) as 'avg'"
			+ "		FROM feedback f"
			+ "		group by postid) as fb on p.id = fb.pid"
			+ " join house h on p.house_id = h.id"
			+ " join user u on u.id = h.user_id"
			+ " where p.is_active = true and u.is_active = true and h.enable = true"
			+ " and p.status = 'CENSORED' and p.start_date <= ?1 and p.end_date >= ?1"
			+ " GROUP BY h.id"
			+ " ORDER BY (CASE p.verify WHEN 'VERIFIED' THEN 1 ELSE 2 END) ASC, p.post_cost DESC, fb.avg DESC Limit 0, 8", nativeQuery = true)
	List<PostModel> findTop8(Date dateNow);

	@Query("SELECT new vn.edu.fpt.capstone.dto.DashBoardData(MONTH(p.createdDate), COUNT(p))"
			+ " FROM PostModel p"
			+ " WHERE YEAR(p.createdDate) = ?1"
			+ " GROUP BY MONTH(p.createdDate)"
			+ " ORDER BY MONTH(p.createdDate)")
	List<DashBoardData> getDataPostingDashBoardAdmin(int year);

	@Query("select count(p) > 0"
			+ " from PostModel p where p.post_code = ?1")
	boolean checkExistCode(String code);

	@Query("select new vn.edu.fpt.capstone.response.HouseResponse(p.house.name, p.house.typeOfRental.name, p.verify) from PostModel p where p.enable = true AND p.isActive = true"
			+ " AND p.house.user.isActive = true"
			+ " AND p.house.enable = true"
			+ " AND p.status = 'CENSORED'"
			+ " AND p.endDate >= ?1"
			+ " AND p.startDate <= ?1"
			+ " GROUP BY p.house.name"
			+ " ORDER BY p.postCost")
	List<HouseResponse> getAllHouseNamePosting(Date date);

	@Query("select p from PostModel p where p.enable = true AND p.isActive = true AND p.house.name LIKE %?1%"
			+ " AND p.house.user.isActive = true"
			+ " AND p.house.enable = true"
			+ " AND p.status = 'CENSORED'"
			+ " AND p.endDate >= ?2"
			+ " AND p.startDate <= ?2"
			+ " GROUP BY p.house.id"
			+ " ORDER BY p.postCost")	
	List<PostModel> getAllPostModelContainKey(String key, Date dateNow);

	
	@Query(value = "SELECT p.* FROM post p"
			+ " left join (SELECT postid as pid, AVG(f.rating) as 'avg'"
			+ "		FROM feedback f"
			+ "		group by postid) as fb on p.id = fb.pid"
			+ " JOIN house h ON p.house_id = h.id"
			+ " JOIN room r ON h.id = r.house_id"
			+ " JOIN house_amenitiess ha ON ha.house_id = h.id"
			+ " JOIN room_amenity ra ON ra.room_id = r.id"
			+ " JOIN user u on u.id = h.user_id"
			
			+ " WHERE p.enable = true AND p.is_active = true "
			+ " AND p.start_date <= :date and p.end_date >= :date"
			+ " AND u.is_active = true and h.enable = true"
			+ " AND r.enable = true AND p.status = 'CENSORED'"		
			+ " AND (:verify = '' or p.verify = :verify)"
			
			+ " AND (r.area >= :minArea or :minArea is null or :minArea = '')"
			+ " AND (r.area <= :maxArea or :maxArea is null or :maxArea = '')"
			+ " AND (h.type_of_rental_id in (:typeOfRentalIds) or COALESCE(:typeOfRentalIds) is null)"
			+ " AND (COALESCE(:roomCategoryIds) is null or r.room_category_id IN (:roomCategoryIds))"
			+ " AND (r.rental_price >= :minPrice or :minPrice is null or :minPrice = '')"
			+ " AND (r.rental_price <= :maxPrice or :maxPrice is null or :maxPrice = '')"
			+ " AND (r.maximum_number_of_people <= :maximumNumberOfPeople or :maximumNumberOfPeople is null or :maximumNumberOfPeople = '')"
			+ " AND (COALESCE(:amenityHouseIds) is null or ha.amenity_id IN (:amenityHouseIds))"
			+ " AND (COALESCE(:amenityRoomIds) is null or ra.amenity_id IN (:amenityRoomIds))"
			+ " AND (:roomMate = '' or r.room_mate = :roomMate)"
			+ " AND (:houseName = '' or h.name LIKE CONCAT('%',:houseName,'%'))"
			+ " GROUP BY h.id"
			+ " ORDER BY (CASE p.verify WHEN 'VERIFIED' THEN 1 ELSE 2 END) ASC, p.post_cost DESC, fb.avg DESC", nativeQuery = true)
	List<PostModel> getListPostModelFilter(@Param("verify") String verify, @Param("minArea") Double minArea,
			@Param("maxArea") Double maxArea, @Param("typeOfRentalIds") List<Long> typeOfRentalIds,
			@Param("roomCategoryIds") List<Long> roomCategoryIds, @Param("minPrice") Integer minPrice,
			@Param("maxPrice") Integer maxPrice, @Param("maximumNumberOfPeople") Integer maximumNumberOfPeople,
			@Param("amenityHouseIds") List<Long> amenityHouseIds, @Param("amenityRoomIds") List<Long> amenityRoomIds, 
			@Param("roomMate") String roomMate, @Param("houseName") String houseName, @Param("date") Date date);

	@Query(value="SELECT p.* FROM post p"
			+ " left join (SELECT postid as pid, AVG(f.rating) as 'avg'"
			+ "		FROM feedback f"
			+ "		group by postid) as fb on p.id = fb.pid"
			+ " join house h on p.house_id = h.id"
			+ " join user u on u.id = h.user_id"
			+ " where p.is_active = true and u.is_active = true and h.enable = true"
			+ " and h.name LIKE %?1%"
			+ " and p.status = 'CENSORED' and p.start_date <= ?2 and p.end_date >= ?2"
			+ " GROUP BY h.id"
			+ " ORDER BY (CASE p.verify WHEN 'VERIFIED' THEN 1 ELSE 2 END) ASC, p.post_cost DESC, fb.avg DESC", nativeQuery = true)
	List<PostModel> getListPage(String key, Date dateNow);
	
	//:#{#customer.firstname}
}
