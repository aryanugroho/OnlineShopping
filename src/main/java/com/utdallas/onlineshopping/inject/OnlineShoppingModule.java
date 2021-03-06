package com.utdallas.onlineshopping.inject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.hubspot.dropwizard.guice.GuiceBundle;
import com.utdallas.onlineshopping.OnlineShoppingApplication;
import com.utdallas.onlineshopping.configurations.OnlineShoppingConfiguration;
import com.utdallas.onlineshopping.models.Product;
import com.utdallas.onlineshopping.payload.request.product.ProductRequest;
import com.utdallas.onlineshopping.resources.CustomerResource;
import com.utdallas.onlineshopping.util.HibernateUtil;
import io.dropwizard.jackson.Jackson;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

public class OnlineShoppingModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        //Bind Resource classes here
        bind(CustomerResource.class).in(Singleton.class);

        //Binding other singleton classes needed
        bind(HibernateUtil.class).in(Singleton.class);
    }

    @Provides
    public GuiceBundle<OnlineShoppingConfiguration> provideGuiceBundle()
    {
        return OnlineShoppingApplication.getGuiceBundle();
    }

    @Provides
    @Singleton
    public ObjectMapper proviceObjectMapper()
    {
        ObjectMapper objectMapper = Jackson.newObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        objectMapper.registerModule(new JodaModule());
        return objectMapper;
    }

    @Provides
    @Singleton
    public ModelMapper provideModelMapper()
    {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setAmbiguityIgnored(true);

        //Configuring modelMapper to set the productId of Product class from
        //categoryPrefix of ProductRequest class.
        //This is to avoid not null error when persisting the product in DB
        modelMapper.addMappings(new PropertyMap<ProductRequest, Product>() {
            @Override
            protected void configure() {
                map().setProductId(source.getCategoryPrefix());
            }
        });

        return modelMapper;
    }
}
