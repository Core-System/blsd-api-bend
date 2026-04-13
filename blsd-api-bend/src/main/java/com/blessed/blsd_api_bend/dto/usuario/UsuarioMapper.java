package com.blessed.blsd_api_bend.dto.usuario;

import com.blessed.blsd_api_bend.dto.cliente.ClienteRequestDTO;
import com.blessed.blsd_api_bend.dto.funcionario.FuncionarioRequestDTO;
import com.blessed.blsd_api_bend.model.entity.Cliente;
import com.blessed.blsd_api_bend.model.entity.Funcionario;
import com.blessed.blsd_api_bend.model.entity.Usuario;

import java.time.LocalDateTime;

public class UsuarioMapper {

        public static void mapCamposIguais(Usuario usuario, UsuarioRequestDTO dto) {
            usuario.setEmail(dto.getEmail());
            usuario.setSenha(dto.getSenha());
            usuario.setNome(dto.getNome());
            usuario.setDataCriacao(LocalDateTime.now());
            usuario.setUrlFoto(dto.getUrlFoto());

        }

        public static Cliente of(ClienteRequestDTO dto) {
            Cliente cliente = new Cliente();
            mapCamposIguais(cliente, dto);
            cliente.setDataNasc(dto.getDataNasc());
            cliente.setTelefone(dto.getTelefone());
            return cliente;
        }

        public static Funcionario of(FuncionarioRequestDTO dto) {
            Funcionario funcionario = new Funcionario();
            mapCamposIguais(funcionario, dto);
            funcionario.setCpf(dto.getCpf());
            funcionario.setEmpresa(dto.getEmpresa());
            return funcionario;
        }

        public static UsuarioTokenDTO of(Usuario usuario, String token){
            UsuarioTokenDTO usuarioTokenDTO = new UsuarioTokenDTO();

            usuarioTokenDTO.setId(usuario.getId());
            usuarioTokenDTO.setEmail(usuario.getEmail());
            usuarioTokenDTO.setNome(usuario.getNome());
            usuarioTokenDTO.setToken(token);

            return usuarioTokenDTO;
        }





}


