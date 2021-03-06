package pl.zzpj.spacer.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.zzpj.spacer.model.Comment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentRepository extends MongoRepository<Comment, UUID> {
    Optional<List<Comment>> findAllByPictureId(String pictureId);

    Optional<List<Comment>> findAllByOwner(String owner);
}
