package com.utdallas.onlineshopping.action.category;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.utdallas.onlineshopping.action.Action;
import com.utdallas.onlineshopping.db.hibernate.CategoryHibernateDAO;
import com.utdallas.onlineshopping.exceptions.ConflictingRequestException;
import com.utdallas.onlineshopping.exceptions.InternalErrorException;
import com.utdallas.onlineshopping.exceptions.NotFoundException;
import com.utdallas.onlineshopping.models.Category;
import com.utdallas.onlineshopping.payload.request.category.CategoryRequest;
import com.utdallas.onlineshopping.payload.response.category.CategoryResponse;
import com.utdallas.onlineshopping.util.HibernateUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.modelmapper.ModelMapper;

@Slf4j
public class UpdateCategoriesAction implements Action<CategoryResponse>
{
    private final HibernateUtil hibernateUtil;
    private ModelMapper modelMapper;
    private CategoryRequest categoryRequest;
    private String categoryId;

    public UpdateCategoriesAction withRequest(CategoryRequest categoryRequest)
    {
        this.categoryRequest = categoryRequest;
        return this;
    }

    public UpdateCategoriesAction withId(String id)
    {
        this.categoryId = id;
        return this;
    }

    @Inject
    public UpdateCategoriesAction(Provider<HibernateUtil> hibernateUtilProvider, ModelMapper modelMapper)
    {
        this.hibernateUtil = hibernateUtilProvider.get();
        this.modelMapper = modelMapper;
    }

    @Override
    public CategoryResponse invoke()
    {
        CategoryHibernateDAO categoryHibernateDAO = hibernateUtil.getCategoryHibernateDAO();

        Optional<Category> categoryOptional = categoryHibernateDAO.findById(categoryId);
        Category category;

        if( categoryOptional.isPresent() )
        {
            category = categoryOptional.get();
        }
        else
        {
            throw new NotFoundException("No category matching the given ID");
        }

        try
        {
            if( !Strings.isNullOrEmpty(categoryRequest.getCategoryName()) )
                category.setCategoryName(categoryRequest.getCategoryName());
            if( !Strings.isNullOrEmpty(categoryRequest.getCategoryId()) )
                category.setCategoryId(categoryRequest.getCategoryId());
            Category newCategory = categoryHibernateDAO.update(category);
            return modelMapper.map(newCategory, CategoryResponse.class);
        }
        catch( HibernateException e )
        {
            log.error(String.valueOf(e.getCause()));
            String errorMessage = e.getCause().getMessage();
            if( errorMessage.contains("Duplicate entry") )
                throw new ConflictingRequestException("Category ID already exists");
            throw new InternalErrorException(e.getMessage());
        }
    }
}
