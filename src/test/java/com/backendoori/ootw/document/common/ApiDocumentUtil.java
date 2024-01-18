package com.backendoori.ootw.document.common;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;
import org.springframework.restdocs.payload.FieldDescriptor;

public interface ApiDocumentUtil {

    static OperationRequestPreprocessor getDocumentRequest() {
        return preprocessRequest(
            modifyUris()
                .scheme("https")
                .host("docs.api.com")
                .removePort(),
            prettyPrint());
    }

    static OperationResponsePreprocessor getDocumentResponse() {
        return preprocessResponse(prettyPrint());
    }

    static FieldDescriptor field(String name, Object type, String description) {
        return fieldWithPath(name)
            .type(type)
            .description(description);
    }

    static FieldDescriptor field(String name, Object type, String description, boolean required) {
        if (required) {
            return field(name, type, description);
        }

        return fieldWithPath(name)
            .type(type)
            .description(description)
            .optional();
    }

}
