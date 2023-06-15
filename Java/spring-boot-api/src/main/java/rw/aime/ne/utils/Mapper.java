package rw.aime.ne.utils;

import org.modelmapper.ModelMapper;
import rw.aime.ne.models.User;

public class Mapper {

    public static ModelMapper modelMapper = new ModelMapper();

    public static User getUserFromDTO(Object object) {
        return modelMapper.map(object, User.class);
    }


}
