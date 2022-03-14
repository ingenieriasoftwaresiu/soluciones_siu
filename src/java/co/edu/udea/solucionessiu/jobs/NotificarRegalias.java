/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package co.edu.udea.solucionessiu.jobs;

import co.edu.udea.solucionessiu.dao.NotificacionMailSigepDAO;
import co.edu.udea.solucionessiu.dao.NotificarRegaliasDAO;
import co.edu.udea.solucionessiu.dao.impl.NotificacionMailSigepDAOImpl;
import co.edu.udea.solucionessiu.dao.impl.NotificarRegaliasDAOImpl;
import co.edu.udea.solucionessiu.dto.ParticipanteRegalias;
import co.edu.udea.solucionessiu.exception.GIDaoException;
import java.util.ArrayList;
import java.util.List;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author jorge.correa
 */
public class NotificarRegalias implements Job{
    
    private static final String CODIGO_ESTADO_ACTIVO = "A";

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        new GIDaoException("Iniciando tarea NotificarRegalias");
        
        List<String> destinatarios = new ArrayList<String>();
                      
        NotificarRegaliasDAO notificarRegaliasDAO = new NotificarRegaliasDAOImpl();
        List<ParticipanteRegalias> participantes = null;
        
        NotificacionMailSigepDAO notificacionMailDAO = new NotificacionMailSigepDAOImpl();
        
        try{       
            participantes = notificarRegaliasDAO.obtenerPorEstado(CODIGO_ESTADO_ACTIVO);
        }catch(GIDaoException e){
            new GIDaoException("Se generó un error recuperando los destinatarios", e);
        }
        
        if (participantes != null){
            
            for (ParticipanteRegalias participante : participantes){
                destinatarios.add(participante.getCorreo());
                participante = null;
            }
            
            if (destinatarios.size() > 0){
                try{
                    notificacionMailDAO.notificarRegalias(destinatarios);                
                }catch(GIDaoException e){
                    new GIDaoException("Se generó un error enviando la notificación", e);
                }
            }
        }
        
        new GIDaoException("Total personas notificadas: " + destinatarios.size());            
        new GIDaoException("Finalizando tarea NotificarRegalias");
    }            
}
