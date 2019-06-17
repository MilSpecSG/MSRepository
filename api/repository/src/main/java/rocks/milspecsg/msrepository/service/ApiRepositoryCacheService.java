package rocks.milspecsg.msrepository.service;

import org.bson.types.ObjectId;
import rocks.milspecsg.msrepository.api.RepositoryCacheService;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;
import rocks.milspecsg.msrepository.model.Dbo;

import java.util.Optional;

public abstract class ApiRepositoryCacheService<T extends Dbo> extends ApiCacheInvalidationService<T> implements RepositoryCacheService<T> {

    public ApiRepositoryCacheService(ConfigurationService configurationService) {
        super(configurationService);
    }

    @Override
    public Optional<T> getOne(ObjectId id) {
        return getOne(dbo -> dbo.getId().equals(id));
    }

}