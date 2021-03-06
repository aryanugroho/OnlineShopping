package com.utdallas.onlineshopping.action.category;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.utdallas.onlineshopping.action.Action;
import com.utdallas.onlineshopping.exceptions.NotFoundException;
import com.utdallas.onlineshopping.models.Category;
import com.utdallas.onlineshopping.payload.response.category.CategoryResponse;
import com.utdallas.onlineshopping.util.HibernateUtil;
import org.modelmapper.ModelMapper;

public class GetCategoryAction implements Action<CategoryResponse>
{
    private final HibernateUtil hibernateUtil;
    private ModelMapper modelMapper;
    private String categoryId;

    @Inject
    public GetCategoryAction(Provider<HibernateUtil> hibernateUtilProvider, ModelMapper modelMapper)
    {
        this.hibernateUtil = hibernateUtilProvider.get();
        this.modelMapper = modelMapper;
    }

    public GetCategoryAction withId(String id)
    {
        this.categoryId = id;
        return this;
    }

    public CategoryResponse invoke()
    {
        Optional<Category> categoryOptional = hibernateUtil.getCategoryHibernateDAO().findById(categoryId);
        if( categoryOptional.isPresent() )
        {
            return modelMapper.map(categoryOptional.get(), CategoryResponse.class);
        }
        else
        {
            throw new NotFoundException("No category matching the given ID");
        }
    }
}
