/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package co.edu.udea.solucionessiu.jobs;

import co.edu.udea.solucionessiu.exception.GIDaoException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;

/**
 * Web application lifecycle listener.
 *
 * @author jorge.correa
 */
public class ArrancarJobs implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
       try{
            new ProgramacionNotificarVencimientoPedidos().crearProgramacion();
            new ProgramacionNotificarVencimientoDocumentos().crearProgramacion();
            new ProgramacionNotificarVencimientoPlanMejoramiento().crearProgramacion();
            new ProgramacionNotificarVencimientoPlanCalibracionEquiposASIU().crearProgramacion();
            new ProgramacionNotificarVencimientoContratosNalesAnt().crearProgramacion();
            new ProgramacionNotificarVencimientoContratosNalesAct().crearProgramacion();
            new ProgramacionNotificarVencimientoContratosInternales().crearProgramacion();
            new ProgramacionNotificarVencimientoContratosPS().crearProgramacion();
            new ProgramacionNotificarVencimientoPlanMejoramientoLEC().crearProgramacion();
            new ProgramacionNotificarVencimientoViaticosTiquetesAnt().crearProgramacion();
            new ProgramacionNotificarVencimientoViaticosTiquetesAct().crearProgramacion();
            new ProgramacionNotificarVencimientoCalibracionLEC().crearProgramacion();
            new ProgramacionNotificarVencimientoMntoPretvoEqCi().crearProgramacion();
            new ProgramacionNotificarControlInsumos().crearProgramacion();
            new ProgramacionNotificarVencimientoPlanMntoSIU().crearProgramacion();
            new ProgramacionNotificarVencimientoCartera().crearProgramacion();
        }catch (Exception ex) {
            new GIDaoException("Se generó un error al arrancar los jobs de la aplicación", ex);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
       try{
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.shutdown(false);
        } catch (Exception ex) {
            new GIDaoException("Se generó un error al detener los jobs de la aplicación Soluciones SIU", ex);
        }
    }
}
