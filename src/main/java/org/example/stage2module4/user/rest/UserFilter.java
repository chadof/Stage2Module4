package org.example.stage2module4.user.rest;

import org.example.stage2module4.User;
import org.springframework.data.jpa.domain.Specification;import org.springframework.util.StringUtils;

public record UserFilter(String nameStarts, String emailStarts) {
    public Specification<User> toSpecification() {
        return nameStartsSpec()
                .and(emailStartsSpec());
    }
private Specification<User> nameStartsSpec() {
    return ((root, query, cb) -> StringUtils.hasText(nameStarts)
? cb.like(cb.lower(root.get("name")), nameStarts.toLowerCase() + "%")
: null);
}

private Specification<User> emailStartsSpec() {
    return ((root, query, cb) -> StringUtils.hasText(emailStarts)
            ? cb.like(cb.lower(root.get("name")), emailStartsSpec() + "%")
            : null);
}}