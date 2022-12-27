package ca.skylinedata.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import ca.skylinedata.model.Note;

@Repository
public interface NoteRepository extends PagingAndSortingRepository<Note, Long> {

	@Query(value = "SELECT * FROM NOTE WHERE (author=?1 or shared) and (upper(text) like upper(?2) or upper(name) like upper(?2)) AND upper(category) like upper(?3)",
		    countQuery = "SELECT count(*) FROM NOTE WHERE (author=?1 or shared) and (upper(text) like upper(?2) or upper(name) like upper(?2)) AND upper(category) like upper(?3)",
		    nativeQuery = true)
	Page<Note> findByAuthorAndCategoryAndTextIgnoreCase(String author, String text, String category, Pageable pageable);
	
	@Query(value = "SELECT * FROM NOTE WHERE (author=?1 or shared) and upper(category)=upper(?2)",
		    countQuery = "SELECT count(*) FROM NOTE WHERE (author=?1 or shared) and upper(category)=upper(?2)",
		    nativeQuery = true)
	Page<Note> findByAuthorAndCategoryIgnoreCase(String author, String category, Pageable pageable);
	
	@Query(value = "SELECT * FROM NOTE WHERE (author=?1 or shared) and (upper(text) like upper(?2) or upper(name) like upper(?2))",
		    countQuery = "SELECT count(*) FROM NOTE WHERE (author=?1 or shared) and (upper(text) like upper('?2') or upper(name) like upper(?2))",
		    nativeQuery = true)
	Page<Note> findByAuthorAndTextIgnoreCase(String author, String text, Pageable pageable);
	
	List<Note> findByAuthorOrderByCategory(String author);
	List<Note> findByAuthorOrderByIdDesc(String author);
//	by date last edited
	List<Note> findByAuthorOrderByDtModifiedDesc(String author, Pageable pageable);
	List<Note> findByAuthorOrderByIdDesc(String author, Pageable pageable);

	@Query(value = "SELECT * FROM NOTE WHERE id=?1 and (author=?2 or shared)", nativeQuery = true)
	Optional<Note> findByIdAndAuthor(Long id, String author);
	long countByAuthor(String author);

	@Query(value = "SELECT distinct category FROM NOTE WHERE (author=?1 or shared) and upper(category) like ?2 ORDER BY 1", nativeQuery = true)
	List<String> findCategories(String author, String termLike);

	@Transactional
	void deleteByIdAndAuthor(Long id, String author);

}
