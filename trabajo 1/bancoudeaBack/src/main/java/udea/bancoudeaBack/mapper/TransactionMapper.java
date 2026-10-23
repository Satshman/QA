package udea.bancoudeaBack.mapper;

import udea.bancoudeaBack.DTO.TransactionDTO;
import udea.bancoudeaBack.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TransactionMapper {
    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);
    TransactionDTO toDTO(Transaction transaction);
}
