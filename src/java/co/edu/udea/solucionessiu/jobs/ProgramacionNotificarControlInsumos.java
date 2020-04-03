/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package co.edu.udea.solucionessiu.jobs;

import co.edu.udea.solucionessiu.exception.GIDaoException;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;
import org.quartz.impl.StdSchedulerFactory;

/**
 *
 * @author jorge.correaj
 */
public class ProgramacionNotificarControlInsumos {
    
    public void crearProgramacion(){
        
        Integer intError=0;
        
        try {
            
            new GIDaoException("Inicio la programación de la tarea NotificarControlInsumos");
                                        
            intError = 1;
            
            Scheduler scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.start(); 
            
            // Creación de una instancia de JobDetail.
            JobDetail jobDetail = new JobDetail("NotificarControlInsumosJob", scheduler.DEFAULT_GROUP, NotificarControlInsumos.class);
            
             intError = 2;
            
            // Se crea el trigger para ejecución todos los días.            
             Trigger trigger = TriggerUtils.makeDailyTrigger(6, 30);
             trigger.setName("tgNotificarControlInsumos");
             trigger.setGroup("grupoSIU");
             trigger.setPriority(1);
                                                  
             intError = 3;

            // Registro dentro del Scheduler.            
            scheduler.scheduleJob(jobDetail, trigger);
                        
             intError = 4;
             
             new GIDaoException("Finalizó la programación de la tarea NotificarControlInsumos.");

        } catch(Exception e) {
            new GIDaoException("Se generó un error al ejecutar la tarea NotificarControlInsumos. Variable error = " +  intError, e);
        }
    }
}
