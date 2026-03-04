package com.kasolution.api.controller;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResponse<T> {

     private boolean sucesso;

     private String mensagem;

     private T dados;



     public static <T> ApiResponse<T> ok(T dados){
        return ApiResponse.<T>builder().sucesso(true).dados(dados).build();
     }


     public static <T> ApiResponse<T> ok(String mensagemRetorno, T dados){
         return ApiResponse.<T>builder().sucesso(true).mensagem(mensagemRetorno).dados(dados).build();
     }


     public static <T> ApiResponse<T> erro(String msgErro){
         return ApiResponse.<T>builder().sucesso(false).mensagem(msgErro).build();
     }

}
