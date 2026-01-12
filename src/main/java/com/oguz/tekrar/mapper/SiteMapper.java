package com.oguz.tekrar.mapper;

import com.oguz.tekrar.dto.SiteRequest;
import com.oguz.tekrar.dto.SiteResponse;
import com.oguz.tekrar.entity.Site;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SiteMapper {


    Site toEntity(SiteRequest dto);

    SiteResponse toDto(Site site);

    List<SiteResponse> toSiteResponseList(List<Site> sites);

    void updateEntityFromRequest(SiteRequest dto, @MappingTarget Site site);
}
